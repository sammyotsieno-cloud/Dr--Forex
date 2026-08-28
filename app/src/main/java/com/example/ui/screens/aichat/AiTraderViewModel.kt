package com.example.ui.screens.aichat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DrForexDatabase
import com.example.data.local.entity.LearnedPatternEntity
import com.example.data.local.entity.TradeObservationEntity
import com.example.data.local.entity.VoiceConfigEntity
import com.example.domain.ai.CognitiveTier
import com.example.domain.ai.GuidanceAlert
import com.example.domain.ai.GuidanceEngine
import com.example.domain.ai.QuestionEngine
import com.example.domain.reasoning.ReasoningAndDecisionEngine
import com.example.domain.reasoning.TradeDecision
import com.example.domain.voice.VoiceEngine
import com.example.domain.voice.VoiceInputState
import com.example.domain.voice.VoicePlaybackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ChatBackgroundTheme(val displayName: String, val description: String) {
    PURPLE_NEBULA("Purple Nebula", "Refined dark violet radial cosmic gradient"),
    CYBER_GRID("Cyber Grid", "High-tech matrix grid with neon accents"),
    OBSIDIAN_MINIMAL("Obsidian Minimal", "Sleek ultra-dark obsidian glass"),
    QUANTUM_INDIGO("Quantum Indigo", "Deep indigo-violet dimensional tone"),
    LIGHT_STUDIO("Light Studio", "Crisp bright lilac design studio")
}

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val sender: String, // "USER" or "AI"
    val content: String,
    val tier: CognitiveTier = CognitiveTier.ELI3,
    val decision: TradeDecision? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiTraderUiState(
    val selectedTier: CognitiveTier = CognitiveTier.ELI3,
    val chatBackground: ChatBackgroundTheme = ChatBackgroundTheme.PURPLE_NEBULA,
    val messages: List<ChatMessage> = emptyList(),
    val currentDecision: TradeDecision? = null,
    val guidanceAlerts: List<GuidanceAlert> = emptyList(),
    val isAnalyzing: Boolean = false,
    val voiceConfig: VoiceConfigEntity = VoiceConfigEntity(),
    val activeSymbol: String = "EUR/USD",
    val activeTimeframe: String = "15M"
)

class AiTraderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DrForexDatabase.getDatabase(application)
    private val candleDao = db.candleDao()
    private val datasetDao = db.datasetDao()
    private val voiceConfigDao = db.voiceConfigDao()
    private val knowledgeDao = db.knowledgeDao()
    private val configDao = db.researchConfigDao()

    val voiceEngine = VoiceEngine(application, viewModelScope)

    val playbackState: StateFlow<VoicePlaybackState> = voiceEngine.playbackState
    val inputState: StateFlow<VoiceInputState> = voiceEngine.inputState

    val learnedPatterns: StateFlow<List<LearnedPatternEntity>> = knowledgeDao.getAllPatterns()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentObservations: StateFlow<List<TradeObservationEntity>> = knowledgeDao.getRecentObservations(10)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiState = MutableStateFlow(AiTraderUiState())
    val uiState: StateFlow<AiTraderUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeVoiceConfig()
    }

    private fun observeVoiceConfig() {
        viewModelScope.launch {
            voiceConfigDao.getVoiceConfigFlow().collect { config ->
                val activeConfig = config ?: VoiceConfigEntity()
                val tier = when (activeConfig.explanationTier) {
                    "TRADER" -> CognitiveTier.TRADER
                    "QUANT" -> CognitiveTier.QUANT
                    else -> CognitiveTier.ELI3
                }
                _uiState.value = _uiState.value.copy(
                    voiceConfig = activeConfig,
                    selectedTier = tier
                )
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Seed initial greeting
            val greeting = "👋 Hello! I am Dr. Forex, your personal AI market analyst and trading mentor. You can ask me to analyze the market, explain trading in simple words ('Explain like I'm 3'), or test strategies with voice."
            val initialMsg = ChatMessage(sender = "AI", content = greeting, tier = CognitiveTier.ELI3)

            _uiState.value = _uiState.value.copy(
                messages = listOf(initialMsg)
            )

            // Evaluate active dataset
            runMarketAnalysis()
        }
    }

    fun setCognitiveTier(tier: CognitiveTier) {
        _uiState.value = _uiState.value.copy(selectedTier = tier)
        viewModelScope.launch(Dispatchers.IO) {
            val current = _uiState.value.voiceConfig
            voiceConfigDao.insertOrUpdate(current.copy(explanationTier = tier.id))
        }
    }

    fun setChatBackground(theme: ChatBackgroundTheme) {
        _uiState.value = _uiState.value.copy(chatBackground = theme)
    }

    fun clearChat() {
        val greeting = "👋 Chat history cleared. Ask me anything about ${uiState.value.activeSymbol} market setups, risk calculations, or trading concepts!"
        _uiState.value = _uiState.value.copy(
            messages = listOf(ChatMessage(sender = "AI", content = greeting, tier = _uiState.value.selectedTier))
        )
    }

    fun runMarketAnalysis() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)

            val datasets = datasetDao.getAllDatasets().firstOrNull() ?: emptyList()
            val activeDataset = datasets.firstOrNull()
            val symbol = activeDataset?.symbol ?: "EUR/USD"
            val tf = activeDataset?.timeframe ?: "15M"

            val candles = if (activeDataset != null) {
                candleDao.getCandlesListByDatasetId(activeDataset.datasetId)
            } else {
                emptyList()
            }

            val config = configDao.getConfig()
            val capital = config?.initialCapital ?: 20000.0
            val riskPct = config?.defaultRiskPerTradePercent ?: 1.0
            val spread = config?.defaultSpreadPips ?: 1.5

            val decision = ReasoningAndDecisionEngine.analyzeMarketAndDecide(
                symbol = symbol,
                timeframe = tf,
                candles = candles,
                accountBalance = capital,
                riskPercent = riskPct,
                dataQuality = activeDataset?.qualityScore?.toDouble() ?: 98.0
            )

            val alerts = GuidanceEngine.generateGuidance(decision, spread, riskPct)

            _uiState.value = _uiState.value.copy(
                currentDecision = decision,
                guidanceAlerts = alerts,
                activeSymbol = symbol,
                activeTimeframe = tf,
                isAnalyzing = false
            )
        }
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(sender = "USER", content = text)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMsg
        )

        viewModelScope.launch(Dispatchers.IO) {
            val responseText = QuestionEngine.processQuery(
                userQuery = text,
                tier = _uiState.value.selectedTier,
                latestDecision = _uiState.value.currentDecision,
                patterns = learnedPatterns.value,
                recentObservations = recentObservations.value
            )

            val aiMsg = ChatMessage(
                sender = "AI",
                content = responseText,
                tier = _uiState.value.selectedTier,
                decision = _uiState.value.currentDecision
            )

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + aiMsg
            )

            if (_uiState.value.voiceConfig.autoPlayVoice) {
                voiceEngine.speak(responseText, _uiState.value.voiceConfig)
            }
        }
    }

    fun speakText(text: String) {
        voiceEngine.speak(text, _uiState.value.voiceConfig)
    }

    fun stopVoice() {
        voiceEngine.stop()
    }

    fun startVoiceInput() {
        voiceEngine.startListening { recognizedText ->
            sendUserMessage(recognizedText)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.destroy()
    }
}
