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
)

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
