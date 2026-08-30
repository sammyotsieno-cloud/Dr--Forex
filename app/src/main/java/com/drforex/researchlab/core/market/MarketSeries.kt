package com.drforex.researchlab.core.market

import com.drforex.researchlab.core.time.MarketTime

/**
 * Immutable chronological collection of market candles belonging to one
 * instrument and one timeframe.
 *
 * MarketSeries is intentionally a raw-data structure. Technical,
 * structural, and statistical interpretations belong in higher-level
 * research engines.
 *
 * The series never fabricates missing data and never silently repairs
 * chronological gaps.
 */
data class MarketSeries(
    val instrument: MarketInstrument,
    val timeframe: MarketTimeframe,
    val candles: List<MarketCandle>
) {

    init {
        require(candles.all { it.instrument == instrument }) {
            "All candles must belong to the series instrument."
        }

        require(candles.all { it.timeframe == timeframe }) {
            "All candles must belong to the series timeframe."
        }

        require(
            candles.zipWithNext().all { (current, next) ->
                current.openTime.isBefore(next.openTime)
            }
        ) {
            "Market candles must be strictly chronological."
        }
    }

    /**
     * Number of candles in the series.
     */
    val size: Int
        get() = candles.size

    /**
     * True when the series contains no candles.
     */
    val isEmpty: Boolean
        get() = candles.isEmpty()

    /**
     * First candle in chronological order.
     */
    val first: MarketCandle?
        get() = candles.firstOrNull()

    /**
     * Last candle in chronological order.
     */
    val last: MarketCandle?
        get() = candles.lastOrNull()

    /**
     * Returns candles whose opening time falls within the requested
     * inclusive time range.
     */
    fun between(
        start: MarketTime,
        end: MarketTime
    ): List<MarketCandle> {
        require(start.isAtOrBefore(end)) {
            "Range start must not be after range end."
        }

        return candles.filter { candle ->
            candle.openTime.isAtOrAfter(start) &&
                candle.openTime.isAtOrBefore(end)
        }
    }

    /**
     * Returns only candles that were fully closed and therefore
     * observable at the supplied point in time.
     *
     * This is the primary point-in-time access path for research code.
     */
    fun availableAt(time: MarketTime): List<MarketCandle> =
        candles.filter { it.isClosedAt(time) }

    /**
     * Returns the latest closed candle available at the supplied time.
     */
    fun latestClosedAt(time: MarketTime): MarketCandle? =
        candles.lastOrNull { it.isClosedAt(time) }

    /**
     * Returns the previous candle relative to the supplied reference
     * candle, provided that the previous candle was already closed at
     * the reference candle's close time.
     *
     * The reference candle must belong to this series.
     */
    fun previousClosed(
        reference: MarketCandle
    ): MarketCandle? {
        require(reference.instrument == instrument) {
            "Reference candle must belong to the series instrument."
        }

        require(reference.timeframe == timeframe) {
            "Reference candle must belong to the series timeframe."
        }

        val index = candles.indexOf(reference)

        if (index <= 0) return null

        val referenceCloseTime = reference.closeTime

        return candles
            .subList(0, index)
            .lastOrNull { it.isClosedAt(referenceCloseTime) }
    }

    /**
     * Detects chronological gaps based on the expected timeframe duration.
     *
     * This is intentionally diagnostic rather than automatic repair.
     * Missing market data must never silently be fabricated.
     */
    fun findGaps(): List<MarketGap> {
        if (candles.size < 2) return emptyList()

        return candles
            .zipWithNext()
            .mapNotNull { (previous, current) ->
                val expectedOpen =
                    previous.openTime.plusSeconds(timeframe.durationSeconds)

                if (current.openTime.isAfter(expectedOpen)) {
                    MarketGap(
                        expectedAt = expectedOpen,
                        actualNextAt = current.openTime
                    )
                } else {
                    null
                }
            }
    }
}

/**
 * Represents a detected gap in an otherwise chronological market series.
 *
 * A gap records what opening time was expected and when the next actual
 * candle begins. It does not attempt to infer or manufacture the missing
 * candles.
 */
data class MarketGap(
    val expectedAt: MarketTime,
    val actualNextAt: MarketTime
) {

    init {
        require(expectedAt.isBefore(actualNextAt)) {
            "Actual next candle time must be after the expected candle time."
        }
    }
}
