package com.example.domain.model

import com.example.domain.engine.ValidationError

/**
 * Encapsulates metadata and header inspection outcomes for a selected CSV file.
 * Used during the initial stage of Market Data Import (Phase 2 Milestone 2.1).
 */
data class CsvInspectionResult(
    val fileName: String,
    val fileSizeBytes: Long? = null,
    val formattedFileSize: String = "Unknown",
    val headers: List<String> = emptyList(),
    val rawHeaderLine: String = "",
    val headerErrors: List<ValidationError> = emptyList(),
    val isHeaderValid: Boolean = false,
    val samplePreviewRows: List<List<String>> = emptyList(),
    val isFileEmpty: Boolean = false,
    val errorMessage: String? = null
)
