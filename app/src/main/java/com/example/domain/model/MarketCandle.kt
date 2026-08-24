package com.example.domain.model

/**
 * Represents a single Point-In-Time Market Candle (OHLCV).
 *
 * Scientific Integrity Constraint:
 * A candle is only "known" once the period has completely elapsed.
 * During backtesting, indicators and strategies must not access this candle's close
 * until the candle timestamp + duration has elapsed, avoiding look-ahead bias.
 */
data class MarketCandle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double = 0.0
) {
    init {
        require(timestamp > 0) { "Timestamp must be positive" }
    }

    val isBullish: Boolean get() = close > open
    val isBearish: Boolean get() = close < open
    val isDoji: Boolean get() = close == open
    val range: Double get() = high - low
    val body: Double get() = Math.abs(close - open)

    /**
     * Checks if OHLC relationship is physically valid.
     */
    val isPhysicallyValid: Boolean
        get() = open > 0 && high > 0 && low > 0 && close > 0 &&
                high >= low &&
                high >= open &&
                high >= close &&
                low <= open &&
                low <= close &&
                volume >= 0
}
