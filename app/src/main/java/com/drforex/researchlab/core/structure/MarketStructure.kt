package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketInstrument
import com.drforex.researchlab.core.market.MarketTimeframe
import com.drforex.researchlab.core.time.MarketTime

/**

* Point-in-time representation of confirmed market structure.

* 

* MarketStructure describes the sequence and classification of confirmed

* swing points available at [asOf].

* 

* It does not produce a trading signal. Its purpose is to represent

* structural evidence that was knowable at a specific point in time.
  */
  data class MarketStructure(
  val instrument: MarketInstrument,
  val timeframe: MarketTimeframe,
  val asOf: MarketTime,
  val points: List<StructurePoint>,
  val direction: StructureDirection
  ) {
  
  /**
  
  * Returns the portion of this structure that was knowable at [time].
  
  * 
  
  * The original [asOf] timestamp is a hard information boundary.
  
  * A historical structure snapshot cannot acquire information merely
  
  * because a caller requests a later time.
  
  * 
  
  * When [time] is earlier than [asOf], swing points whose confirmation
  
  * occurred after [time] are removed.
  
  * 
  
  * When [time] is equal to or later than [asOf], the structure remains
  
  * bounded by its original snapshot and therefore cannot expose evidence
  
  * beyond [asOf].
  
  * 
  
  * Direction is recalculated from the surviving structural points so
  
  * that the returned direction describes the information actually
  
  * available at the requested point in time.
    */
    fun knownAt(time: MarketTime): MarketStructure {
    val effectiveTime =
    if (time.isBefore(asOf)) {
    time
    } else {
    asOf
    }
    
    val knownPoints =
    points.filter { point ->
    point.swing.isConfirmedAt(effectiveTime)
    }
    
    return copy(
    asOf = effectiveTime,
    points = knownPoints,
    direction = determineDirection(knownPoints)
    )
    }
  
  companion object {
  
   /**
  * Determines structural direction from classified structural points.
  *
  * Only classified points participate in the directional assessment.
  * The most recent six classified points are used so that direction
  * reflects recent structural evidence rather than the entire history.
  *
  * This is deliberately a pure structural operation:
  *
  * StructurePoint list
  *        ↓
  * recent classified evidence
  *        ↓
  * bullish/bearish structural evidence
  *        ↓
  * StructureDirection
  *
  * It does not inspect market candles, generate signals, execute
  * trades, or make profitability claims.
  */
 internal fun determineDirection(
     points: List<StructurePoint>
 ): StructureDirection {
     val recentPoints =
         points
             .asSequence()
             .filter {
                 it.classification != StructureClassification.UNCLASSIFIED
             }
             .takeLast(6)
             .toList()

     if (recentPoints.isEmpty()) {
         return StructureDirection.UNKNOWN
     }

     val bullishSignals =
         recentPoints.count {
             it.classification == StructureClassification.HIGHER_HIGH ||
                 it.classification == StructureClassification.HIGHER_LOW
         }

     val bearishSignals =
         recentPoints.count {
             it.classification == StructureClassification.LOWER_HIGH ||
                 it.classification == StructureClassification.LOWER_LOW
         }

     return when {
         bullishSignals > bearishSignals ->
             StructureDirection.BULLISH

         bearishSignals > bullishSignals ->
             StructureDirection.BEARISH

         bullishSignals > 0 && bearishSignals > 0 ->
             StructureDirection.TRANSITION

         else ->
             StructureDirection.RANGE
     }
 }
  
  }
  }

/**

* A confirmed swing interpreted in relation to previous structural evidence.
* 
* Classification is assigned by the market-structure analysis stage.
* This type does not independently infer whether a swing is higher,
* lower, or unclassified.
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
