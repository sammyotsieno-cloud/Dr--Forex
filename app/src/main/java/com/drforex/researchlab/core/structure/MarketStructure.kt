package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketInstrument
import com.drforex.researchlab.core.market.MarketTimeframe
import com.drforex.researchlab.core.time.MarketTime

/**
 * Point-in-time representation of confirmed market structure.
 *
 * MarketStructure describes the sequence and classification of
 * confirmed swing points. It does not produce a trade signal.
 */
data class MarketStructure(
    val instrument: MarketInstrument,
    val timeframe: MarketTimeframe,
    val asOf: MarketTime,
    val points: List<StructurePoint>,
    val direction: StructureDirection
) {

    /**
     * Returns a copy of the market structure containing only points
     * confirmed at or before [time], with structural direction re-evaluated.
     */
    fun knownAt(time: MarketTime): MarketStructure {
        val knownPoints = points.filter { it.swing.isConfirmedAt(time) }
        val recentPoints = knownPoints.filter {
            it.classification != StructureClassification.UNCLASSIFIED
        }.takeLast(6)

        val direction = when {
            recentPoints.isEmpty() -> StructureDirection.UNKNOWN
            else -> {
                val bullishSignals = recentPoints.count {
                    it.classification == StructureClassification.HIGHER_HIGH ||
                        it.classification == StructureClassification.HIGHER_LOW
                }
                val bearishSignals = recentPoints.count {
                    it.classification == StructureClassification.LOWER_HIGH ||
                        it.classification == StructureClassification.LOWER_LOW
                }
                when {
                    bullishSignals > bearishSignals -> StructureDirection.BULLISH
                    bearishSignals > bullishSignals -> StructureDirection.BEARISH
                    bullishSignals > 0 && bearishSignals > 0 -> StructureDirection.TRANSITION
                    else -> StructureDirection.RANGE
                }
            }
        }

        return copy(
            asOf = if (asOf.isBefore(time)) asOf else time,
            points = knownPoints,
            direction = direction
        )
    }
}

/**
 * A confirmed swing interpreted in relation to previous structure.
 */
data class StructurePoint(
    val swing: SwingPoint,
    val classification: StructureClassification
)

/**
 * Structural classification of a confirmed swing.
 */
enum class StructureClassification {
    HIGHER_HIGH,
    HIGHER_LOW,
    LOWER_HIGH,
    LOWER_LOW,
    UNCLASSIFIED
}

/**
 * Current structural direction inferred from confirmed swing relationships.
 */
enum class StructureDirection {
    BULLISH,
    BEARISH,
    RANGE,
    TRANSITION,
    UNKNOWN
}
