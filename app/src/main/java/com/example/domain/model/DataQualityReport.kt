package com.example.domain.model

enum class DataQualityClassification(val displayName: String) {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    CRITICAL_ISSUES("Critical Issues");

    companion object {
        fun fromScore(score: Int): DataQualityClassification = when {
            score >= 90 -> EXCELLENT
            score >= 75 -> GOOD
            score >= 50 -> FAIR
            else -> CRITICAL_ISSUES
        }
    }
}

/**
 * Immutable audit report representing research-grade data quality inspection
 * on an imported market candle series.
 */
data class DataQualityReport(
    val datasetId: String,
    val detectedTimeframe: String,
    val expectedIntervalMinutes: Long,
    val totalCandleCount: Int,
    val firstTimestamp: Long,
    val lastTimestamp: Long,
    val gapCount: Int,
    val abnormalGapCount: Int,
    val maxAbnormalGapDurationMinutes: Long,
    val weekendGapCount: Int,
    val priceSpikeCount: Int,
    val flatlineCount: Int,
    val longestFlatlineStreak: Int,
    val isVolumeAvailable: Boolean,
    val zeroVolumeCount: Int,
    val zeroVolumeStreak: Int,
    val priceIntegrityIssueCount: Int,
    val continuityScore: Int,
    val priceIntegrityScore: Int,
    val flatlineScore: Int,
    val volumeScore: Int,
    val overallQualityScore: Int,
    val qualityClassification: DataQualityClassification,
    val summaryIssues: List<String> = emptyList()
)
