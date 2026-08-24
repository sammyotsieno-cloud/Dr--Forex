package com.example.domain.model

/**
 * System-wide laboratory research parameters.
 */
data class ResearchConfiguration(
    val baseCurrency: String = "KSh",
    val initialCapital: Double = 20000.0,
    val defaultRiskPerTradePercent: Double = 1.0,
    val defaultInstrument: String = "EUR/USD",
    val defaultExecutionTimeframe: String = "M15",
    val contextTimeframes: List<String> = listOf("H1", "H4"),
    val defaultSpreadPips: Double = 1.5,
    val defaultSlippagePips: Double = 0.5,
    val maxDrawdownLimitPercent: Double = 15.0
)
