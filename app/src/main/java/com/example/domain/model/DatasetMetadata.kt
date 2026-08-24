package com.example.domain.model

enum class ValidationStatus {
    UNCHECKED,
    VALID,
    WARNINGS_FOUND,
    INVALID
}

/**
 * Metadata descriptor for imported historical market datasets.
 */
data class DatasetMetadata(
    val datasetId: String,
    val name: String,
    val symbol: String,
    val timeframe: String,
    val startDate: Long,
    val endDate: Long,
    val rowCount: Int,
    val source: String,
    val timezone: String = "UTC",
    val validationStatus: ValidationStatus = ValidationStatus.UNCHECKED,
    val isDevelopmentSample: Boolean = false,
    val validationSummary: String = "Not validated"
)
