package com.example.domain.model

/**
 * Represents the outcome of importing CSV candle rows into the Room database.
 * Milestone 2.3: CSV Candle Import into Room.
 */
data class CsvImportResult(
    val isSuccess: Boolean,
    val datasetId: String,
    val datasetName: String,
    val fileName: String,
    val totalRowsRead: Int,
    val importedCount: Int,
    val skippedCount: Int,
    val rejectedCount: Int,
    val rejectedRowErrors: List<CsvRowRejection> = emptyList(),
    val summary: String,
    val startDate: Long? = null,
    val endDate: Long? = null
)

/**
 * Details of an individual rejected CSV data row with row number and reason.
 */
data class CsvRowRejection(
    val rowNumber: Int,
    val reason: String,
    val rawContent: String = ""
)
