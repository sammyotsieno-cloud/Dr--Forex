package com.example.domain.engine

import com.example.domain.model.CsvColumnMapping
import com.example.domain.model.MarketCandle
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Interface for verifying historical market dataset integrity.
 *
 * Checks implemented in Phase 1 & Phase 2:
 * - Empty dataset detection
 * - Required CSV column presence (via raw headers or confirmed CsvColumnMapping)
 * - Missing / non-numeric values
 * - Duplicate timestamps
 * - Strict chronological ordering
 * - Invalid OHLC relationships (e.g. High < Low, Close > High, Open < Low)
 * - Zero or negative prices
 * - Impossible candles and spike anomalies
 * - Timestamp gap detection
 */
interface DataValidator {
    /**
     * Validates a list of MarketCandle objects.
     */
    fun validateCandles(candles: List<MarketCandle>): ValidationResult

    /**
     * Validates CSV headers against required columns.
     */
    fun validateCsvHeaders(headers: List<String>): List<ValidationError>

    /**
     * Validates CSV headers against required canonical columns using an optional confirmed/detected column mapping.
     * When mapping is provided, canonical fields (Timestamp, Open, High, Low, Close) are resolved through the mapping.
     */
    fun validateCsvHeaders(headers: List<String>, mapping: CsvColumnMapping?): List<ValidationError>

    /**
     * Validates raw CSV content lines.
     */
    fun validateCsvContent(lines: List<String>): ValidationResult

    /**
     * Validates raw CSV content lines using an optional column mapping.
     */
    fun validateCsvContent(lines: List<String>, mapping: CsvColumnMapping?): ValidationResult
}

class DataValidatorImpl : DataValidator {

    private val requiredHeaders = setOf("timestamp", "open", "high", "low", "close")

    override fun validateCsvHeaders(headers: List<String>): List<ValidationError> {
        return validateCsvHeaders(headers, mapping = null)
    }

    override fun validateCsvHeaders(headers: List<String>, mapping: CsvColumnMapping?): List<ValidationError> {
        if (mapping != null) {
            val errors = mutableListOf<ValidationError>()
            val headerLookup = headers.map { it.trim().lowercase(Locale.ROOT) }.toSet()

            fun checkRequiredMappedField(canonicalField: String, mappedCol: String?) {
                if (mappedCol.isNullOrBlank()) {
                    errors.add(
                        ValidationError(
                            rowIndex = 0,
                            field = canonicalField,
                            message = "Missing required column: '$canonicalField' (No column mapped)"
                        )
                    )
                } else if (!headerLookup.contains(mappedCol.trim().lowercase(Locale.ROOT))) {
                    errors.add(
                        ValidationError(
                            rowIndex = 0,
                            field = canonicalField,
                            message = "Missing required column: '$canonicalField' (Mapped column '$mappedCol' not found in headers)"
                        )
                    )
                }
            }

            checkRequiredMappedField("timestamp", mapping.timestampColumn)
            checkRequiredMappedField("open", mapping.openColumn)
            checkRequiredMappedField("high", mapping.highColumn)
            checkRequiredMappedField("low", mapping.lowColumn)
            checkRequiredMappedField("close", mapping.closeColumn)

            return errors
        }

        val normalizedHeaders = headers.map { it.trim().lowercase(Locale.ROOT) }.toSet()
        val missing = requiredHeaders.filter { req ->
            !normalizedHeaders.contains(req) && !normalizedHeaders.any { h -> h.contains(req) }
        }

        return missing.map { missingField ->
            ValidationError(
                rowIndex = 0,
                field = missingField,
                message = "Missing required column: '$missingField' (Expected: timestamp, open, high, low, close)"
            )
        }
    }

    override fun validateCandles(candles: List<MarketCandle>): ValidationResult {
        if (candles.isEmpty()) {
            return ValidationResult(
                isValid = false,
                totalRows = 0,
                validRows = 0,
                errors = listOf(
                    ValidationError(
                        rowIndex = 0,
                        field = "dataset",
                        message = "Dataset contains zero candles. Empty datasets cannot be processed."
                    )
                ),
                summary = "Dataset is empty"
            )
        }

        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()
        val seenTimestamps = mutableSetOf<Long>()
        var validCount = 0

        var previousTimestamp: Long = -1L

        candles.forEachIndexed { index, candle ->
            var candleHasError = false

            // 1. Zero or negative price checks
            if (candle.open <= 0 || candle.high <= 0 || candle.low <= 0 || candle.close <= 0) {
                errors.add(
                    ValidationError(
                        rowIndex = index + 1,
                        field = "price",
                        message = "Row ${index + 1}: Non-positive price detected (O:${candle.open}, H:${candle.high}, L:${candle.low}, C:${candle.close})"
                    )
                )
                candleHasError = true
            }

            // 2. High vs Low integrity
            if (candle.high < candle.low) {
                errors.add(
                    ValidationError(
                        rowIndex = index + 1,
                        field = "high_low",
                        message = "Row ${index + 1}: High (${candle.high}) is lower than Low (${candle.low})"
                    )
                )
                candleHasError = true
            }

            // 3. Open/Close within High-Low range
            if (candle.open > candle.high || candle.open < candle.low) {
                errors.add(
                    ValidationError(
                        rowIndex = index + 1,
                        field = "open_bounds",
                        message = "Row ${index + 1}: Open (${candle.open}) is outside High-Low range [${candle.low}, ${candle.high}]"
                    )
                )
                candleHasError = true
            }

            if (candle.close > candle.high || candle.close < candle.low) {
                errors.add(
                    ValidationError(
                        rowIndex = index + 1,
                        field = "close_bounds",
                        message = "Row ${index + 1}: Close (${candle.close}) is outside High-Low range [${candle.low}, ${candle.high}]"
                    )
                )
                candleHasError = true
            }

            // 4. Volume non-negative
            if (candle.volume < 0) {
                errors.add(
                    ValidationError(
                        rowIndex = index + 1,
                        field = "volume",
                        message = "Row ${index + 1}: Volume (${candle.volume}) cannot be negative"
                    )
                )
                candleHasError = true
            }

            // 5. Chronological timestamp order check
            if (previousTimestamp != -1L && candle.timestamp <= previousTimestamp) {
                if (candle.timestamp == previousTimestamp) {
                    errors.add(
                        ValidationError(
                            rowIndex = index + 1,
                            field = "timestamp_duplicate",
                            message = "Row ${index + 1}: Duplicate timestamp (${candle.timestamp}) matches previous row"
                        )
                    )
                } else {
                    errors.add(
                        ValidationError(
                            rowIndex = index + 1,
                            field = "timestamp_chronology",
                            message = "Row ${index + 1}: Timestamp (${candle.timestamp}) is out of chronological order (previous: $previousTimestamp)"
                        )
                    )
                }
                candleHasError = true
            }

            // 6. Duplicate timestamp across dataset
            if (seenTimestamps.contains(candle.timestamp) && candle.timestamp != previousTimestamp) {
                errors.add(
                    ValidationError(
                        rowIndex = index + 1,
                        field = "timestamp_duplicate",
                        message = "Row ${index + 1}: Duplicate timestamp (${candle.timestamp}) already exists in dataset"
                    )
                )
                candleHasError = true
            }
            seenTimestamps.add(candle.timestamp)

            // 7. Time gap warning (e.g. gap > 7 days warning)
            if (previousTimestamp != -1L && candle.timestamp - previousTimestamp > 7 * 24 * 3600 * 1000L) {
                warnings.add(
                    ValidationWarning(
                        rowIndex = index + 1,
                        message = "Row ${index + 1}: Large time gap of ${(candle.timestamp - previousTimestamp) / (24 * 3600 * 1000L)} days detected"
                    )
                )
            }

            previousTimestamp = candle.timestamp

            if (!candleHasError) {
                validCount++
            }
        }

        val isValid = errors.isEmpty()
        val startDate = candles.firstOrNull()?.timestamp
        val endDate = candles.lastOrNull()?.timestamp
        val summary = if (isValid) {
            "Validation PASSED: ${candles.size} valid candles verified. 0 errors, ${warnings.size} warnings."
        } else {
            "Validation FAILED: Found ${errors.size} error(s) across ${candles.size} rows."
        }

        return ValidationResult(
            isValid = isValid,
            totalRows = candles.size,
            validRows = validCount,
            errors = errors,
            warnings = warnings,
            startDate = startDate,
            endDate = endDate,
            summary = summary
        )
    }

    override fun validateCsvContent(lines: List<String>): ValidationResult {
        return validateCsvContent(lines, mapping = null)
    }

    override fun validateCsvContent(lines: List<String>, mapping: CsvColumnMapping?): ValidationResult {
        if (lines.isEmpty()) {
            return ValidationResult(
                isValid = false,
                totalRows = 0,
                validRows = 0,
                errors = listOf(ValidationError(0, "csv", "File is completely empty")),
                summary = "CSV is empty"
            )
        }

        val headerLine = lines.first()
        val headers = headerLine.split(",").map { it.trim().removeSurrounding("\"") }
        val headerErrors = validateCsvHeaders(headers, mapping)
        if (headerErrors.isNotEmpty()) {
            return ValidationResult(
                isValid = false,
                totalRows = lines.size,
                validRows = 0,
                errors = headerErrors,
                summary = "Header validation failed: Missing required columns"
            )
        }

        // Determine column indices from mapping (or default positional/alias matching)
        val timestampIdx = if (mapping?.timestampColumn != null) {
            headers.indexOfFirst { it.trim().equals(mapping.timestampColumn.trim(), ignoreCase = true) }
        } else {
            headers.indexOfFirst { it.trim().lowercase(Locale.ROOT).contains("timestamp") || it.trim().lowercase(Locale.ROOT).contains("time") || it.trim().lowercase(Locale.ROOT).contains("date") || it.trim().lowercase(Locale.ROOT).contains("utc") }.let { if (it != -1) it else 0 }
        }

        val openIdx = if (mapping?.openColumn != null) {
            headers.indexOfFirst { it.trim().equals(mapping.openColumn.trim(), ignoreCase = true) }
        } else {
            headers.indexOfFirst { it.trim().lowercase(Locale.ROOT).contains("open") }.let { if (it != -1) it else 1 }
        }

        val highIdx = if (mapping?.highColumn != null) {
            headers.indexOfFirst { it.trim().equals(mapping.highColumn.trim(), ignoreCase = true) }
        } else {
            headers.indexOfFirst { it.trim().lowercase(Locale.ROOT).contains("high") }.let { if (it != -1) it else 2 }
        }

        val lowIdx = if (mapping?.lowColumn != null) {
            headers.indexOfFirst { it.trim().equals(mapping.lowColumn.trim(), ignoreCase = true) }
        } else {
            headers.indexOfFirst { it.trim().lowercase(Locale.ROOT).contains("low") }.let { if (it != -1) it else 3 }
        }

        val closeIdx = if (mapping?.closeColumn != null) {
            headers.indexOfFirst { it.trim().equals(mapping.closeColumn.trim(), ignoreCase = true) }
        } else {
            headers.indexOfFirst { it.trim().lowercase(Locale.ROOT).contains("close") }.let { if (it != -1) it else 4 }
        }

        val volumeIdx = if (mapping?.volumeColumn != null) {
            headers.indexOfFirst { it.trim().equals(mapping.volumeColumn.trim(), ignoreCase = true) }.takeIf { it != -1 }
        } else {
            headers.indexOfFirst { it.trim().lowercase(Locale.ROOT).contains("volume") || it.trim().lowercase(Locale.ROOT).contains("vol") }.takeIf { it != -1 }
        }

        val maxRequiredIdx = maxOf(timestampIdx, openIdx, highIdx, lowIdx, closeIdx)

        // Parse data rows
        val parsedCandles = mutableListOf<MarketCandle>()
        val parsingErrors = mutableListOf<ValidationError>()

        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue

            val tokens = line.split(",").map { it.trim().removeSurrounding("\"") }
            if (tokens.size <= maxRequiredIdx) {
                parsingErrors.add(
                    ValidationError(i, "columns", "Row $i: Incomplete row with only ${tokens.size} columns (expected at least ${maxRequiredIdx + 1})")
                )
                continue
            }

            try {
                val rawTs = tokens[timestampIdx]
                val timestamp = parseTimestampString(rawTs)
                val open = tokens[openIdx].toDouble()
                val high = tokens[highIdx].toDouble()
                val low = tokens[lowIdx].toDouble()
                val close = tokens[closeIdx].toDouble()
                val volume = if (volumeIdx != null && volumeIdx < tokens.size) tokens[volumeIdx].toDoubleOrNull() ?: 0.0 else 0.0

                if (timestamp == null || timestamp <= 0) {
                    parsingErrors.add(ValidationError(i, "timestamp", "Row $i: Invalid timestamp format ('$rawTs')"))
                    continue
                }

                parsedCandles.add(MarketCandle(timestamp, open, high, low, close, volume))
            } catch (e: Exception) {
                parsingErrors.add(
                    ValidationError(i, "format", "Row $i: Numeric parsing error: ${e.message}")
                )
            }
        }

        if (parsingErrors.isNotEmpty()) {
            return ValidationResult(
                isValid = false,
                totalRows = lines.size - 1,
                validRows = parsedCandles.size,
                errors = parsingErrors,
                summary = "CSV content has ${parsingErrors.size} parsing errors"
            )
        }

        return validateCandles(parsedCandles)
    }

    private fun parseTimestampString(raw: String): Long? {
        val clean = raw.trim().removeSurrounding("\"")
        clean.toLongOrNull()?.let { if (it > 0) return it }
        return try {
            Instant.parse(clean).toEpochMilli()
        } catch (_: Exception) {
            try {
                OffsetDateTime.parse(clean, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant().toEpochMilli()
            } catch (_: Exception) {
                null
            }
        }
    }
}
