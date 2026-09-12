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
        val candles = listOf(
            candle(
                openTime = "2026-01-01T10:00:00Z",
                open = 1.1000,
                high = 1.1010,
                low = 1.0990,
                close = 1.1005
            ),
            candle(
                openTime = "2026-01-01T10:15:00Z",
                open = 1.1005,
                high = 1.1025,
                low = 1.1000,
                close = 1.1020
            ),
            candle(
                openTime = "2026-01-01T10:30:00Z",
                open = 1.1020,
                high = 1.1030,
                low = 1.1010,
                close = 1.1025
            )
        )

        val series = MarketSeries(candles)

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
        val series =
            MarketSeries(emptyList())

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

    private fun candle(
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
            instrument = MarketInstrument.forex(
                baseCurrency = "EUR",
                quoteCurrency = "USD"
            ),
            timeframe = MarketTimeframe.M15,
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
