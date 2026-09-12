package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketCandle
import com.drforex.researchlab.core.market.MarketInstrument
import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.market.MarketTimeframe
import com.drforex.researchlab.core.time.MarketTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class MarketContextAnalyzerTest {

@Test
fun `analysis is point in time safe`() {
    val instrument =
        MarketInstrument.forex(
            baseCurrency = "EUR",
            quoteCurrency = "USD"
        )

    val timeframe =
        MarketTimeframe.M15

    val candles = listOf(
        candle(
            instrument = instrument,
            timeframe = timeframe,
            openTime = "2026-01-01T10:00:00Z",
            open = 1.1000,
            high = 1.1010,
            low = 1.0990,
            close = 1.1005
        ),
        candle(
            instrument = instrument,
            timeframe = timeframe,
            openTime = "2026-01-01T10:15:00Z",
            open = 1.1005,
            high = 1.1025,
            low = 1.1000,
            close = 1.1020
        ),
        candle(
            instrument = instrument,
            timeframe = timeframe,
            openTime = "2026-01-01T10:30:00Z",
            open = 1.1020,
            high = 1.1030,
            low = 1.1010,
            close = 1.1025
        )
    )

    val series =
        MarketSeries(
            instrument = instrument,
            timeframe = timeframe,
            candles = candles
        )

    val asOf =
        MarketTime(
            Instant.parse(
                "2026-01-01T10:20:00Z"
            )
        )

    val context =
        MarketContextAnalyzer()
            .analyze(
                series = series,
                asOf = asOf
            )

    assertEquals(
        asOf,
        context.asOf
    )

    assertTrue(
        context.fairValueGaps.none {
            it.confirmationTime.isAfter(asOf)
        }
    )

    assertTrue(
        context.liquidityEvents.none {
            it.confirmationTime.isAfter(asOf)
        }
    )

    assertTrue(
        context.displacements.none {
            it.confirmationTime.isAfter(asOf)
        }
    )
}

@Test
fun `empty series produces empty structural context`() {
    val instrument =
        MarketInstrument.forex(
            baseCurrency = "EUR",
            quoteCurrency = "USD"
        )

    val timeframe =
        MarketTimeframe.M15

    val series =
        MarketSeries(
            instrument = instrument,
            timeframe = timeframe,
            candles = emptyList()
        )

    val asOf =
        MarketTime(
            Instant.parse(
                "2026-01-01T10:00:00Z"
            )
        )

    val context =
        MarketContextAnalyzer()
            .analyze(
                series = series,
                asOf = asOf
            )

    assertEquals(
        asOf,
        context.asOf
    )

    assertTrue(
        context.structure.points.isEmpty()
    )

    assertEquals(
        StructureDirection.UNKNOWN,
        context.structure.direction
    )

    assertTrue(
        context.recentBreaks.isEmpty()
    )

    assertTrue(
        context.recentChangeOfCharacters.isEmpty()
    )

    assertTrue(
        context.supportResistanceZones.isEmpty()
    )

    assertTrue(
        context.liquidityEvents.isEmpty()
    )

    assertTrue(
        context.fairValueGaps.isEmpty()
    )

    assertTrue(
        context.displacements.isEmpty()
    )
}

@Test
fun `structure excludes swing whose confirmation occurs after snapshot`() {
    val instrument =
        MarketInstrument.forex(
            baseCurrency = "EUR",
            quoteCurrency = "USD"
        )

    val timeframe =
        MarketTimeframe.M15

    val series =
        MarketSeries(
            instrument = instrument,
            timeframe = timeframe,
            candles = listOf(
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:00:00Z",
                    open = 1.1000,
                    high = 1.1010,
                    low = 1.0990,
                    close = 1.1005
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:15:00Z",
                    open = 1.1005,
                    high = 1.1020,
                    low = 1.1000,
                    close = 1.1015
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:30:00Z",
                    open = 1.1015,
                    high = 1.1050,
                    low = 1.1010,
                    close = 1.1040
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:45:00Z",
                    open = 1.1040,
                    high = 1.1030,
                    low = 1.1020,
                    close = 1.1025
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T11:00:00Z",
                    open = 1.1025,
                    high = 1.1020,
                    low = 1.1005,
                    close = 1.1010
                )
            )
        )

    val beforeConfirmation =
        MarketTime(
            Instant.parse(
                "2026-01-01T11:14:59Z"
            )
        )

    val atConfirmation =
        MarketTime(
            Instant.parse(
                "2026-01-01T11:15:00Z"
            )
        )

    val beforeContext =
        MarketContextAnalyzer()
            .analyze(
                series = series,
                asOf = beforeConfirmation
            )

    val confirmedContext =
        MarketContextAnalyzer()
            .analyze(
                series = series,
                asOf = atConfirmation
            )

    assertTrue(
        beforeContext.structure.points.none {
            it.swing.time == MarketTime(
                Instant.parse(
                    "2026-01-01T10:30:00Z"
                )
            )
        }
    )

    assertTrue(
        confirmedContext.structure.points.any {
            it.swing.time == MarketTime(
                Instant.parse(
                    "2026-01-01T10:30:00Z"
                )
        }
    )

    assertTrue(
        confirmedContext.structure.points.all {
            it.swing.confirmationTime.isAtOrBefore(atConfirmation)
        }
    )
}

@Test
fun `earlier snapshot excludes later confirmed structure while preserving chronology`() {
    val instrument =
        MarketInstrument.forex(
            baseCurrency = "EUR",
            quoteCurrency = "USD"
        )

    val timeframe =
        MarketTimeframe.M15

    val series =
        MarketSeries(
            instrument = instrument,
            timeframe = timeframe,
            candles = listOf(
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:00:00Z",
                    open = 1.1000,
                    high = 1.1010,
                    low = 1.0990,
                    close = 1.1005
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:15:00Z",
                    open = 1.1005,
                    high = 1.1040,
                    low = 1.1000,
                    close = 1.1030
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:30:00Z",
                    open = 1.1030,
                    high = 1.1050,
                    low = 1.1020,
                    close = 1.1040
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T10:45:00Z",
                    open = 1.1040,
                    high = 1.1035,
                    low = 1.1010,
                    close = 1.1020
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T11:00:00Z",
                    open = 1.1020,
                    high = 1.1025,
                    low = 1.1000,
                    close = 1.1010
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T11:15:00Z",
                    open = 1.1010,
                    high = 1.1060,
                    low = 1.1005,
                    close = 1.1050
                ),
                candle(
                    instrument = instrument,
                    timeframe = timeframe,
                    openTime = "2026-01-01T11:30:00Z",
                    open = 1.1050,
                    high = 1.1040,
                    low = 1.1020,
                    close = 1.1030
                )
            )
        )

    val earlierSnapshot =
        MarketTime(
            Instant.parse(
                "2026-01-01T11:15:00Z"
            )
        )

    val laterSnapshot =
        MarketTime(
            Instant.parse(
                "2026-01-01T11:45:00Z"
            )
        )

    val earlierContext =
        MarketContextAnalyzer()
            .analyze(
                series = series,
                asOf = earlierSnapshot
            )

    val laterContext =
        MarketContextAnalyzer()
            .analyze(
                series = series,
                asOf = laterSnapshot
            )

    assertTrue(
        earlierContext.structure.points.all {
            it.swing.confirmationTime.isAtOrBefore(earlierSnapshot)
        }
    )

    assertTrue(
        laterContext.structure.points.all {
            it.swing.confirmationTime.isAtOrBefore(laterSnapshot)
        }
    )

    assertTrue(
        laterContext.structure.points.size >=
            earlierContext.structure.points.size
    )

    assertTrue(
        earlierContext.structure.points.zipWithNext().all {
            (first, second) ->
            !second.swing.time.isBefore(first.swing.time)
        }
    )

    assertTrue(
        laterContext.structure.points.zipWithNext().all {
            (first, second) ->
            !second.swing.time.isBefore(first.swing.time)
        }
    )
}

private fun candle(
    instrument: MarketInstrument,
    timeframe: MarketTimeframe,
    openTime: String,
    open: Double,
    high: Double,
    low: Double,
    close: Double
): MarketCandle {

    val openInstant =
        Instant.parse(openTime)

    val closeInstant =
        openInstant.plusSeconds(15 * 60)

    return MarketCandle(
        instrument = instrument,
        timeframe = timeframe,
        openTime = MarketTime(openInstant),
        closeTime = MarketTime(closeInstant),
        open = open,
        high = high,
        low = low,
        close = close,
        volume = 0.0
    )
}

     }
