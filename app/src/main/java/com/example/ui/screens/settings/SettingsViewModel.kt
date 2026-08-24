package com.example.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DrForexDatabase
import com.example.data.repository.ResearchConfigRepositoryImpl
import com.example.domain.model.ResearchConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val config: ResearchConfiguration = ResearchConfiguration(),
    val isSaved: Boolean = false,
    val saveMessage: String = ""
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DrForexDatabase.getDatabase(application)
    private val configRepo = ResearchConfigRepositoryImpl(db.researchConfigDao())

    val configFlow: StateFlow<ResearchConfiguration> = configRepo.configFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResearchConfiguration()
        )

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun saveConfiguration(
        baseCurrency: String,
        initialCapital: Double,
        riskPercent: Double,
        instrument: String,
        timeframe: String,
        contextTimeframes: List<String>,
        spread: Double,
        slippage: Double
    ) {
        viewModelScope.launch {
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
            _uiState.value = _uiState.value.copy(
                config = updated,
                isSaved = true,
                saveMessage = "Laboratory settings saved successfully"
            )
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            val default = ResearchConfiguration()
            configRepo.saveConfig(default)
            _uiState.value = _uiState.value.copy(
                config = default,
                isSaved = true,
                saveMessage = "Reset to default parameters (KSh 20,000 / 1% Risk)"
            )
        }
    }
}
