package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle
import java.time.Duration
import java.time.Instant

/**
 * Utilities for deriving higher-timeframe context without allowing
 * future observations to leak into a lower-timeframe observation.
 *
 * This class focuses on temporal alignment. It does not decide
 * whether a higher timeframe is bullish, bearish, or tradable.
 */
object TimeframeFeature {

    /**
     * Returns the latest candle from [higherTimeframeCandles] whose
     * close timestamp is not later than [observationTime].
     *
     * This is the primary point-in-time alignment operation.
     */
    fun latestCompletedCandleAt(
        higherTimeframeCandles: List<MarketCandle>,
        observationTime: Instant
    ): MarketCandle? {

        return higherTimeframeCandles
            .asSequence()
            .filter {
                it.closeTime.isAtOrBefore(
                    com.drforex.researchlab.core.time.MarketTime(observationTime)
                )
            }
            .maxByOrNull {
                it.closeTime.instant
            }
    }

    /**
     * Returns the higher-timeframe candles available at a specific
     * lower-timeframe observation.
     *
     * Only candles completed by [observationTime] are included.
     */
    fun completedCandlesAt(
        higherTimeframeCandles: List<MarketCandle>,
        observationTime: Instant
    ): List<MarketCandle> {

        val marketTime =
            com.drforex.researchlab.core.time.MarketTime(observationTime)

        return higherTimeframeCandles.filter {
            it.closeTime.isAtOrBefore(marketTime)
        }
    }

    /**
     * Calculates the ratio between two candle durations.
     *
     * Useful when validating expected timeframe relationships.
     */
    fun timeframeRatio(
        lowerTimeframe: Duration,
        higherTimeframe: Duration
    ): Double {

        require(
            !lowerTimeframe.isZero &&
                !lowerTimeframe.isNegative
        ) {
            "Lower timeframe must be positive."
        }

        require(
            !higherTimeframe.isZero &&
                !higherTimeframe.isNegative
        ) {
            "Higher timeframe must be positive."
        }

        return higherTimeframe.toMillis().toDouble() /
            lowerTimeframe.toMillis().toDouble()
    }

    /**
     * Determines whether a higher timeframe candle is fully
     * available at the supplied observation time.
     */
    fun isCompletedAt(
        candle: MarketCandle,
        observationTime: Instant
    ): Boolean =
        candle.closeTime.isAtOrBefore(
            com.drforex.researchlab.core.time.MarketTime(observationTime)
        )

    /**
     * Calculates a higher-timeframe feature using only candles that
     * had completed by the observation timestamp.
     *
     * The supplied calculator receives the point-in-time candle
     * history and therefore cannot see later candles through this
     * method.
     */
    fun calculateAt(
        higherTimeframeCandles: List<MarketCandle>,
        observationTime: Instant,
        calculator: (List<MarketCandle>) -> Double?
    ): Double? {

        val available =
            completedCandlesAt(
                higherTimeframeCandles = higherTimeframeCandles,
                observationTime = observationTime
            )

        if (available.isEmpty()) {
            return null
        }

        return calculator(available)
    }
}
