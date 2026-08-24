package com.example.data.repository

import com.example.domain.model.DatasetMetadata
import com.example.domain.model.MarketCandle
import com.example.domain.model.ValidationStatus

/**
 * Provides a synthetic sample dataset solely for development and UI/pipeline testing.
 *
 * Prominently labeled:
 * DEVELOPMENT SAMPLE — NOT REAL MARKET DATA
 */
object SampleDataFactory {

    val sampleDatasetMetadata = DatasetMetadata(
        datasetId = "DS-EURUSD-M15-DEV",
        name = "EUR/USD M15 (Dev Sample)",
        symbol = "EUR/USD",
        timeframe = "M15",
        startDate = 1704067200000L, // 2024-01-01 00:00 UTC
        endDate = 1704157200000L,   // 2024-01-02 01:00 UTC
        rowCount = 100,
        source = "Dr. Forex Synthetic Dev Generator",
        timezone = "UTC",
        validationStatus = ValidationStatus.VALID,
        isDevelopmentSample = true,
        validationSummary = "Validation PASSED (DEVELOPMENT SAMPLE — NOT REAL MARKET DATA): 100 verified test candles"
    )

    /**
     * Generates 100 deterministic test candles for testing data integrity validation.
     */
    fun createSampleCandles(): List<MarketCandle> {
        val candles = mutableListOf<MarketCandle>()
        var basePrice = 1.0950
        val baseTime = 1704067200000L
        val interval = 15 * 60 * 1000L // 15 mins

        for (i in 0 until 100) {
            val delta = Math.sin(i.toDouble() * 0.2) * 0.0015 + (Math.cos(i.toDouble() * 0.1) * 0.0008)
            val open = Math.round((basePrice + delta) * 100000.0) / 100000.0
            val close = Math.round((open + Math.sin(i.toDouble()) * 0.0008) * 100000.0) / 100000.0
            val high = Math.round((Math.max(open, close) + 0.0006) * 100000.0) / 100000.0
            val low = Math.round((Math.min(open, close) - 0.0006) * 100000.0) / 100000.0
            val volume = 150.0 + (i % 20) * 12.5

            candles.add(
                MarketCandle(
                    timestamp = baseTime + (i * interval),
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )
            basePrice = close
        }
        return candles
    }

    /**
     * Creates a CSV formatted string for testing the CSV validation parser.
     */
    fun createSampleCsvString(): String {
        val sb = StringBuilder()
        sb.append("timestamp,open,high,low,close,volume\n")
        createSampleCandles().forEach { c ->
            sb.append("${c.timestamp},${c.open},${c.high},${c.low},${c.close},${c.volume}\n")
        }
        return sb.toString()
    }
}
