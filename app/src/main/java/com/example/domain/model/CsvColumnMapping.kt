package com.example.domain.model

/**
 * Domain model representing the column association between raw CSV header names
 * and Dr. Forex canonical market data fields.
 */
data class CsvColumnMapping(
    val timestampColumn: String? = null,
    val openColumn: String? = null,
    val highColumn: String? = null,
    val lowColumn: String? = null,
    val closeColumn: String? = null,
    val volumeColumn: String? = null
) {
    /**
     * True if all 5 required OHLC fields are non-blank.
     */
    val isComplete: Boolean
        get() = !timestampColumn.isNullOrBlank() &&
                !openColumn.isNullOrBlank() &&
                !highColumn.isNullOrBlank() &&
                !lowColumn.isNullOrBlank() &&
                !closeColumn.isNullOrBlank()

    /**
     * Returns a list of active mappings (canonical field name to raw CSV header).
     */
    fun toFieldMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        timestampColumn?.let { map["timestamp"] = it }
        openColumn?.let { map["open"] = it }
        highColumn?.let { map["high"] = it }
        lowColumn?.let { map["low"] = it }
        closeColumn?.let { map["close"] = it }
        volumeColumn?.let { map["volume"] = it }
        return map
    }
}

/**
 * Validation outcome for CSV column mapping.
 */
data class MappingValidationResult(
    val isValid: Boolean,
    val missingRequiredFields: List<String> = emptyList(),
    val duplicateColumns: Map<String, List<String>> = emptyMap(),
    val errorMessages: List<String> = emptyList()
)
