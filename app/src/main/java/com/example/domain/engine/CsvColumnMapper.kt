package com.example.domain.engine

import com.example.domain.model.CsvColumnMapping
import com.example.domain.model.MappingValidationResult

/**
 * Service for detecting, mapping, and validating raw CSV headers against canonical
 * Dr. Forex market data fields (Timestamp, Open, High, Low, Close, Volume).
 */
interface CsvColumnMapper {
    /**
     * Automatically attempts to map raw CSV headers to canonical fields using
     * deterministic alias recognition.
     */
    fun autoDetectMapping(headers: List<String>): CsvColumnMapping

    /**
     * Validates a column mapping against the available CSV headers, checking for
     * required field presence and duplicate assignments.
     */
    fun validateMapping(mapping: CsvColumnMapping, availableHeaders: List<String>): MappingValidationResult
}

class CsvColumnMapperImpl : CsvColumnMapper {

    private val timestampAliases = listOf(
        "timestamp", "etc/utc", "datetime", "date_time", "date/time",
        "time", "date", "time (utc)", "utc", "gmt", "local_time"
    )

    private val openAliases = listOf(
        "open", "open_price", "open price", "bid_open", "ask_open", "o"
    )

    private val highAliases = listOf(
        "high", "high_price", "high price", "bid_high", "ask_high", "h"
    )

    private val lowAliases = listOf(
        "low", "low_price", "low price", "bid_low", "ask_low", "l"
    )

    private val closeAliases = listOf(
        "close", "close_price", "close price", "bid_close", "ask_close", "c"
    )

    private val volumeAliases = listOf(
        "volume", "vol", "tick_volume", "tick volume", "volume_traded", "ticks", "v"
    )

    override fun autoDetectMapping(headers: List<String>): CsvColumnMapping {
        if (headers.isEmpty()) return CsvColumnMapping()

        val usedHeaders = mutableSetOf<String>()

        val timestamp = findBestMatch(headers, timestampAliases, usedHeaders)
        timestamp?.let { usedHeaders.add(it) }

        val open = findBestMatch(headers, openAliases, usedHeaders)
        open?.let { usedHeaders.add(it) }

        val high = findBestMatch(headers, highAliases, usedHeaders)
        high?.let { usedHeaders.add(it) }

        val low = findBestMatch(headers, lowAliases, usedHeaders)
        low?.let { usedHeaders.add(it) }

        val close = findBestMatch(headers, closeAliases, usedHeaders)
        close?.let { usedHeaders.add(it) }

        val volume = findBestMatch(headers, volumeAliases, usedHeaders)

        return CsvColumnMapping(
            timestampColumn = timestamp,
            openColumn = open,
            highColumn = high,
            lowColumn = low,
            closeColumn = close,
            volumeColumn = volume
        )
    }

    override fun validateMapping(
        mapping: CsvColumnMapping,
        availableHeaders: List<String>
    ): MappingValidationResult {
        val missingFields = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()

        fun validateRequiredField(fieldName: String, colValue: String?) {
            if (colValue.isNullOrBlank()) {
                missingFields.add(fieldName)
                errorMessages.add("Required field '$fieldName' is not mapped.")
            } else if (availableHeaders.isNotEmpty() && !availableHeaders.contains(colValue)) {
                missingFields.add(fieldName)
                errorMessages.add("Mapped column '$colValue' for field '$fieldName' does not exist in CSV headers.")
            }
        }

        // 1. Required fields check
        validateRequiredField("Timestamp", mapping.timestampColumn)
        validateRequiredField("Open", mapping.openColumn)
        validateRequiredField("High", mapping.highColumn)
        validateRequiredField("Low", mapping.lowColumn)
        validateRequiredField("Close", mapping.closeColumn)

        // Volume check (optional, but if provided, must exist in availableHeaders)
        if (!mapping.volumeColumn.isNullOrBlank() && availableHeaders.isNotEmpty() && !availableHeaders.contains(mapping.volumeColumn)) {
            errorMessages.add("Mapped column '${mapping.volumeColumn}' for field 'Volume' does not exist in CSV headers.")
        }

        // 2. Duplicate mappings check
        val fieldToColumn = mutableListOf<Pair<String, String>>()
        mapping.timestampColumn?.let { if (it.isNotBlank()) fieldToColumn.add("Timestamp" to it) }
        mapping.openColumn?.let { if (it.isNotBlank()) fieldToColumn.add("Open" to it) }
        mapping.highColumn?.let { if (it.isNotBlank()) fieldToColumn.add("High" to it) }
        mapping.lowColumn?.let { if (it.isNotBlank()) fieldToColumn.add("Low" to it) }
        mapping.closeColumn?.let { if (it.isNotBlank()) fieldToColumn.add("Close" to it) }
        mapping.volumeColumn?.let { if (it.isNotBlank()) fieldToColumn.add("Volume" to it) }

        val columnUsage = mutableMapOf<String, MutableList<String>>()
        for ((field, col) in fieldToColumn) {
            columnUsage.getOrPut(col) { mutableListOf() }.add(field)
        }

        val duplicates = columnUsage.filter { it.value.size > 1 }
        for ((col, fields) in duplicates) {
            errorMessages.add("Column '$col' is mapped to multiple fields: ${fields.joinToString(", ")}.")
        }

        val isValid = missingFields.isEmpty() && duplicates.isEmpty() && errorMessages.isEmpty()

        return MappingValidationResult(
            isValid = isValid,
            missingRequiredFields = missingFields,
            duplicateColumns = duplicates,
            errorMessages = errorMessages
        )
    }

    private fun findBestMatch(
        headers: List<String>,
        aliases: List<String>,
        usedHeaders: Set<String>
    ): String? {
        val available = headers.filterNot { usedHeaders.contains(it) }

        // 1. Exact case-insensitive match against aliases in priority order
        for (alias in aliases) {
            val matched = available.firstOrNull { it.trim().equals(alias, ignoreCase = true) }
            if (matched != null) return matched
        }

        // 2. Normalized alphanumeric match
        for (alias in aliases) {
            val normalizedAlias = alias.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
            val matched = available.firstOrNull {
                it.replace(Regex("[^a-zA-Z0-9]"), "").lowercase() == normalizedAlias
            }
            if (matched != null) return matched
        }

        return null
    }
}
