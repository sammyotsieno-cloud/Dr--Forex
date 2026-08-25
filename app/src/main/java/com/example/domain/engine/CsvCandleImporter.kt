package com.example.domain.engine

import android.content.ContentResolver
import android.net.Uri
import com.example.domain.model.CsvColumnMapping
import com.example.domain.model.CsvRowRejection
import com.example.domain.model.MarketCandle
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale

/**
 * Result of parsing and validating CSV data rows using confirmed column mappings.
 */
data class CsvParseResult(
    val validCandles: List<MarketCandle>,
    val totalRowsRead: Int,
    val rejectedRows: List<CsvRowRejection>,
    val headerIndices: Map<String, Int> = emptyMap()
)

interface CsvCandleImporter {
    /**
     * Parses an InputStream incrementally, applies the CsvColumnMapping, converts valid rows to
     * MarketCandle objects, checks OHLC integrity rules, and tracks invalid rows.
     */
    fun parseAndValidateRows(
        inputStream: InputStream,
        mapping: CsvColumnMapping
    ): CsvParseResult

    /**
     * Opens a content Uri via ContentResolver and parses rows with mapping.
     */
    fun parseAndValidateUri(
        contentResolver: ContentResolver,
        uri: Uri,
        mapping: CsvColumnMapping
    ): CsvParseResult
}

class CsvCandleImporterImpl(
    private val validator: DataValidator = DataValidatorImpl()
) : CsvCandleImporter {

    override fun parseAndValidateUri(
        contentResolver: ContentResolver,
        uri: Uri,
        mapping: CsvColumnMapping
    ): CsvParseResult {
        val stream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")
        return stream.use { parseAndValidateRows(it, mapping) }
    }

    override fun parseAndValidateRows(
        inputStream: InputStream,
        mapping: CsvColumnMapping
    ): CsvParseResult {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        var line: String? = reader.readLine()

        // 1. Read header line
        while (line != null && line.isBlank()) {
            line = reader.readLine()
        }

        if (line == null) {
            return CsvParseResult(
                validCandles = emptyList(),
                totalRowsRead = 0,
                rejectedRows = listOf(CsvRowRejection(1, "CSV file is empty"))
            )
        }

        val rawHeaders = parseCsvLine(line)
        val headerIndices = buildHeaderIndexMap(rawHeaders, mapping)

        // Validate that all required mapped headers exist
        val missingFields = mutableListOf<String>()
        if (headerIndices["timestamp"] == null) missingFields.add("Timestamp ('${mapping.timestampColumn ?: "unmapped"}')")
        if (headerIndices["open"] == null) missingFields.add("Open ('${mapping.openColumn ?: "unmapped"}')")
        if (headerIndices["high"] == null) missingFields.add("High ('${mapping.highColumn ?: "unmapped"}')")
        if (headerIndices["low"] == null) missingFields.add("Low ('${mapping.lowColumn ?: "unmapped"}')")
        if (headerIndices["close"] == null) missingFields.add("Close ('${mapping.closeColumn ?: "unmapped"}')")

        if (missingFields.isNotEmpty()) {
            return CsvParseResult(
                validCandles = emptyList(),
                totalRowsRead = 0,
                rejectedRows = listOf(
                    CsvRowRejection(
                        rowNumber = 1,
                        reason = "Missing required column headers: ${missingFields.joinToString(", ")}",
                        rawContent = line
                    )
                ),
                headerIndices = headerIndices
            )
        }

        val timestampIdx = headerIndices["timestamp"]!!
        val openIdx = headerIndices["open"]!!
        val highIdx = headerIndices["high"]!!
        val lowIdx = headerIndices["low"]!!
        val closeIdx = headerIndices["close"]!!
        val volumeIdx = headerIndices["volume"]

        val validCandles = mutableListOf<MarketCandle>()
        val rejectedRows = mutableListOf<CsvRowRejection>()
        var dataRowNumber = 1 // Row number in data portion (header is row 1)

        while (true) {
            line = reader.readLine() ?: break
            if (line.isBlank()) continue
            dataRowNumber++

            val tokens = parseCsvLine(line)
            val maxRequiredIdx = maxOf(
                timestampIdx, openIdx, highIdx, lowIdx, closeIdx,
                volumeIdx ?: 0
            )

            if (tokens.size <= maxRequiredIdx) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Incomplete row with ${tokens.size} columns (expected at least ${maxRequiredIdx + 1})",
                        rawContent = line
                    )
                )
                continue
            }

            // 1. Timestamp Parsing
            val rawTimestamp = tokens[timestampIdx]
            val timestamp = try {
                parseTimestampToEpochMillis(rawTimestamp)
            } catch (e: Exception) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Invalid timestamp '$rawTimestamp': ${e.message}",
                        rawContent = line
                    )
                )
                continue
            }

            if (timestamp <= 0L) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Non-positive timestamp value ($timestamp)",
                        rawContent = line
                    )
                )
                continue
            }

            // 2. Open Price Parsing
            val rawOpen = tokens[openIdx]
            val open = rawOpen.toDoubleOrNull()
            if (open == null) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Non-numeric Open price: '$rawOpen'",
                        rawContent = line
                    )
                )
                continue
            }

            // 3. High Price Parsing
            val rawHigh = tokens[highIdx]
            val high = rawHigh.toDoubleOrNull()
            if (high == null) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Non-numeric High price: '$rawHigh'",
                        rawContent = line
                    )
                )
                continue
            }

            // 4. Low Price Parsing
            val rawLow = tokens[lowIdx]
            val low = rawLow.toDoubleOrNull()
            if (low == null) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Non-numeric Low price: '$rawLow'",
                        rawContent = line
                    )
                )
                continue
            }

            // 5. Close Price Parsing
            val rawClose = tokens[closeIdx]
            val close = rawClose.toDoubleOrNull()
            if (close == null) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Non-numeric Close price: '$rawClose'",
                        rawContent = line
                    )
                )
                continue
            }

            // 6. Volume Parsing (Optional)
            var volume = 0.0
            if (volumeIdx != null && volumeIdx < tokens.size) {
                val rawVolume = tokens[volumeIdx]
                if (rawVolume.isNotBlank()) {
                    val parsedVol = rawVolume.toDoubleOrNull()
                    if (parsedVol == null) {
                        rejectedRows.add(
                            CsvRowRejection(
                                rowNumber = dataRowNumber,
                                reason = "Non-numeric Volume: '$rawVolume'",
                                rawContent = line
                            )
                        )
                        continue
                    }
                    if (parsedVol < 0) {
                        rejectedRows.add(
                            CsvRowRejection(
                                rowNumber = dataRowNumber,
                                reason = "Negative Volume ($parsedVol)",
                                rawContent = line
                            )
                        )
                        continue
                    }
                    volume = parsedVol
                }
            }

            // 7. OHLC Physical Integrity Validation
            if (open <= 0 || high <= 0 || low <= 0 || close <= 0) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Non-positive price (O:$open, H:$high, L:$low, C:$close)",
                        rawContent = line
                    )
                )
                continue
            }

            if (high < low) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "High ($high) is lower than Low ($low)",
                        rawContent = line
                    )
                )
                continue
            }

            if (open > high || open < low) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Open ($open) outside High-Low range [$low, $high]",
                        rawContent = line
                    )
                )
                continue
            }

            if (close > high || close < low) {
                rejectedRows.add(
                    CsvRowRejection(
                        rowNumber = dataRowNumber,
                        reason = "Close ($close) outside High-Low range [$low, $high]",
                        rawContent = line
                    )
                )
                continue
            }

            validCandles.add(
                MarketCandle(
                    timestamp = timestamp,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )
        }

        // Sort chronologically by timestamp
        val sortedCandles = validCandles.sortedBy { it.timestamp }

        return CsvParseResult(
            validCandles = sortedCandles,
            totalRowsRead = dataRowNumber - 1,
            rejectedRows = rejectedRows,
            headerIndices = headerIndices
        )
    }

    private fun buildHeaderIndexMap(
        rawHeaders: List<String>,
        mapping: CsvColumnMapping
    ): Map<String, Int> {
        val indices = mutableMapOf<String, Int>()

        fun findIndex(targetColumnName: String?): Int? {
            if (targetColumnName.isNullOrBlank()) return null
            val exact = rawHeaders.indexOfFirst { it.trim().equals(targetColumnName.trim(), ignoreCase = true) }
            return if (exact != -1) exact else null
        }

        findIndex(mapping.timestampColumn)?.let { indices["timestamp"] = it }
        findIndex(mapping.openColumn)?.let { indices["open"] = it }
        findIndex(mapping.highColumn)?.let { indices["high"] = it }
        findIndex(mapping.lowColumn)?.let { indices["low"] = it }
        findIndex(mapping.closeColumn)?.let { indices["close"] = it }
        findIndex(mapping.volumeColumn)?.let { indices["volume"] = it }

        return indices
    }

    /**
     * Splits a CSV line taking quotation marks and whitespace into account.
     */
    fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    sb.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim())
                sb.setLength(0)
            } else {
                sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    /**
     * Parses diverse ISO-8601, ISO with offset, standard UTC, and epoch timestamps deterministically.
     */
    fun parseTimestampToEpochMillis(raw: String): Long {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) throw IllegalArgumentException("Timestamp is empty")

        // 1. Direct Long epoch (millis or seconds)
        val numeric = trimmed.toLongOrNull()
        if (numeric != null && numeric > 0) {
            return if (numeric < 100_000_000_000L) numeric * 1000L else numeric
        }

        // 2. ISO-8601 with Offset (e.g. 2026-08-25T01:00:00+00:00, 2026-08-25T01:00:00Z)
        try {
            return OffsetDateTime.parse(trimmed).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 3. Instant
        try {
            return Instant.parse(trimmed).toEpochMilli()
        } catch (_: Exception) {}

        // 4. ZonedDateTime
        try {
            return ZonedDateTime.parse(trimmed).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 5. Formats with space instead of 'T' (e.g. "2026-08-25 01:00:00+00:00" or "2026-08-25 01:00:00")
        val isoStandardized = trimmed.replace(" ", "T")
        try {
            return OffsetDateTime.parse(isoStandardized).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        try {
            return LocalDateTime.parse(isoStandardized).toInstant(ZoneOffset.UTC).toEpochMilli()
        } catch (_: Exception) {}

        // 6. Date only (e.g. "2026-08-25")
        try {
            return LocalDate.parse(trimmed).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        throw IllegalArgumentException("Unparseable timestamp format: '$raw'")
    }
}
