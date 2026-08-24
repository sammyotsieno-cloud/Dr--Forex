package com.example.domain.model

enum class ExperimentStatus {
    DRAFT,
    CONFIGURED,
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED,
    REJECTED_OVERFIT,
    REJECTED_NEGATIVE_EXPECTANCY,
    REJECTED_EXCESSIVE_DRAWDOWN
}

/**
 * Immutable audit record for an empirical trading research experiment.
 *
 * Every strategy test is assigned a unique sequential ID (e.g. EXP-0001)
 * and persisted permanently for reproducible scientific analysis.
 */
data class Experiment(
    val experimentId: String,
    val timestamp: Long,
    val strategyId: String,
    val strategyName: String,
    val datasetId: String,
    val datasetName: String,
    val instrument: String,
    val timeframe: String,
    val parametersSummary: String,
    val status: ExperimentStatus = ExperimentStatus.CONFIGURED,
    val metrics: PerformanceMetrics? = null,
    val notes: String = "",
    val trainingPeriod: String = "N/A",
    val validationPeriod: String = "N/A",
    val outOfSamplePeriod: String = "N/A",
    val initialCapital: Double = 20000.0,
    val riskPerTradePercent: Double = 1.0,
    val spreadPips: Double = 1.5,
    val slippagePips: Double = 0.5
)
