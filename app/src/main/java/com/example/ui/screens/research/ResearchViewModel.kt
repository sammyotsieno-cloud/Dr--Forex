package com.example.ui.screens.research

import androidx.lifecycle.ViewModel
import com.example.domain.engine.ModuleStatus
import com.example.domain.engine.PipelineModuleInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ResearchUiState(
    val modules: List<PipelineModuleInfo> = emptyList(),
    val selectedModule: PipelineModuleInfo? = null
)

class ResearchViewModel : ViewModel() {

    private val pipelineModules = listOf(
        PipelineModuleInfo(
            id = "mod_validation",
            name = "1. Data Validation",
            phase = 1,
            status = ModuleStatus.READY,
            description = "Validates required columns, chronological sequencing, duplicate timestamps, missing values, and physical OHLC validity bounds.",
            scientificConstraint = "Invalid or corrupted price records are quarantined before reaching the quantitative engine."
        ),
        PipelineModuleInfo(
            id = "mod_indicators",
            name = "2. Indicator Engine",
            phase = 3,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Computes mathematical technical indicators (Moving averages, Oscillators, Volatility bands) point-in-time.",
            scientificConstraint = "Indicators must only use closed candle data at t=0, never peeking at candle close during open."
        ),
        PipelineModuleInfo(
            id = "mod_structure",
            name = "3. Market Structure",
            phase = 3,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Identifies swing highs/lows, trend shifts (BOS/CHoCH), fair value gaps, and liquidity sweeps.",
            scientificConstraint = "Structural break confirmations must wait for candle close verification to prevent false repaint."
        ),
        PipelineModuleInfo(
            id = "mod_strategy",
            name = "4. Strategy Engine",
            phase = 4,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Evaluates formal strategy hypotheses with explicit, deterministic entry, exit, and invalidation rules.",
            scientificConstraint = "Strategy logic is completely isolated from historical outcomes to prevent bias."
        ),
        PipelineModuleInfo(
            id = "mod_backtester",
            name = "5. Backtester",
            phase = 5,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Event-driven historical execution simulator with realistic transaction costs, spread, and slippage.",
            scientificConstraint = "Trades execute on next-candle Open at earliest, with bid/ask spread and realistic slippage deducted."
        ),
        PipelineModuleInfo(
            id = "mod_risk",
            name = "6. Risk Engine",
            phase = 6,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Dynamic position sizing, volatility-adjusted stop losses, maximum portfolio drawdown circuit breakers.",
            scientificConstraint = "Enforces non-negotiable risk limits per trade (e.g. 1%) and catastrophic loss halting."
        ),
        PipelineModuleInfo(
            id = "mod_performance",
            name = "7. Performance Analytics",
            phase = 7,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Multi-dimensional performance evaluation: Sharpe, Sortino, Expectancy, Max Drawdown, Recovery Factor.",
            scientificConstraint = "Rejects evaluating strategies solely on win rate. Prioritizes expectancy and risk-adjusted return."
        ),
        PipelineModuleInfo(
            id = "mod_robustness",
            name = "8. Robustness Testing",
            phase = 8,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Monte Carlo trade reshuffling, parameter plateau sensitivity, spread stress-testing.",
            scientificConstraint = "Detects fragile curve-fitted strategies that fail under slight parameter shifts."
        ),
        PipelineModuleInfo(
            id = "mod_comparison",
            name = "9. Strategy Comparison",
            phase = 7,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Cross-strategy correlation, comparative equity curves, and portfolio diversification analytics.",
            scientificConstraint = "Compares strategies on identical out-of-sample data splits for honest ranking."
        ),
        PipelineModuleInfo(
            id = "mod_oos_validation",
            name = "10. Out-of-Sample Validation",
            phase = 9,
            status = ModuleStatus.NOT_IMPLEMENTED,
            description = "Anchored and rolling walk-forward validation on pristine unseen market datasets.",
            scientificConstraint = "The golden standard against overfitting: Strategies are tested on data completely unexposed to parameter selection."
        )
    )

    private val _uiState = MutableStateFlow(
        ResearchUiState(
            modules = pipelineModules,
            selectedModule = pipelineModules.first()
        )
    )
    val uiState: StateFlow<ResearchUiState> = _uiState.asStateFlow()

    fun selectModule(module: PipelineModuleInfo) {
        _uiState.value = _uiState.value.copy(selectedModule = module)
    }
}
