package com.example.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DrForexDatabase
import com.example.data.local.entity.VoiceConfigEntity
import com.example.data.repository.ResearchConfigRepositoryImpl
import com.example.domain.model.ResearchConfiguration
import com.example.domain.voice.VoiceEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val config: ResearchConfiguration = ResearchConfiguration(),
    val voiceConfig: VoiceConfigEntity = VoiceConfigEntity(),
    val isSaved: Boolean = false,
    val saveMessage: String = ""
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DrForexDatabase.getDatabase(application)
    private val configRepo = ResearchConfigRepositoryImpl(db.researchConfigDao())
    private val voiceConfigDao = db.voiceConfigDao()

    val voiceEngine = VoiceEngine(application, viewModelScope)

    val configFlow: StateFlow<ResearchConfiguration> = configRepo.configFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResearchConfiguration()
        )

    val voiceConfigFlow: StateFlow<VoiceConfigEntity> = voiceConfigDao.getVoiceConfigFlow()
        .map { it ?: VoiceConfigEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VoiceConfigEntity()
        )

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            voiceConfigDao.getVoiceConfigFlow().collect { vConfig ->
                if (vConfig != null) {
                    _uiState.value = _uiState.value.copy(voiceConfig = vConfig)
                }
            }
        }
    }

    fun saveConfiguration(
        baseCurrency: String,
        initialCapital: Double,
        riskPercent: Double,
        instrument: String,
        timeframe: String,
        contextTimeframes: List<String>,
        spread: Double,
        slippage: Double,
        voiceMode: String = "SYSTEM_TTS",
        voiceApiKey: String = "",
        voiceId: String = "default",
        speechRate: Float = 1.0f,
        pitch: Float = 1.0f,
        autoPlayVoice: Boolean = true
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = ResearchConfiguration(
                baseCurrency = baseCurrency.ifBlank { "KSh" },
                initialCapital = if (initialCapital > 0) initialCapital else 20000.0,
                defaultRiskPerTradePercent = if (riskPercent > 0) riskPercent else 1.0,
                defaultInstrument = instrument.ifBlank { "EUR/USD" },
                defaultExecutionTimeframe = timeframe.ifBlank { "M15" },
                contextTimeframes = if (contextTimeframes.isNotEmpty()) contextTimeframes else listOf("H1", "H4"),
                defaultSpreadPips = spread,
                defaultSlippagePips = slippage
            )
            configRepo.saveConfig(updated)

            val vUpdated = VoiceConfigEntity(
                id = 1,
                mode = voiceMode,
                apiKey = voiceApiKey.trim(),
                voiceId = voiceId.trim(),
                speechRate = speechRate,
                pitch = pitch,
                autoPlayVoice = autoPlayVoice
            )
            voiceConfigDao.insertOrUpdate(vUpdated)

            _uiState.value = _uiState.value.copy(
                config = updated,
                voiceConfig = vUpdated,
                isSaved = true,
                saveMessage = "Laboratory & Voice parameters saved successfully"
            )
        }
    }

    fun testVoice(text: String, voiceConfig: VoiceConfigEntity) {
        voiceEngine.speak(text, voiceConfig)
    }

    fun resetToDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            val default = ResearchConfiguration()
            configRepo.saveConfig(default)
            val defaultVoice = VoiceConfigEntity()
            voiceConfigDao.insertOrUpdate(defaultVoice)

            _uiState.value = _uiState.value.copy(
                config = default,
                voiceConfig = defaultVoice,
                isSaved = true,
                saveMessage = "Reset to default parameters"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.destroy()
    }
}

