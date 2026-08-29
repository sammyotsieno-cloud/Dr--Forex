package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Converts confirmed swing points into a chronological market-structure

* representation.

* 

* Classification is performed only from swings confirmed by [asOf].
  */
  class MarketStructureAnalyzer(
  private val swingPointDetector: SwingPointDetector = SwingPointDetector()
  ) {
  
  fun analyze(
  series: MarketSeries,
  asOf: MarketTime,
  leftBars: Int = 2,
  rightBars: Int = 2
  ): MarketStructure {
  
   val confirmedSwings =
     swingPointDetector
         .detect(
             series = series,
             asOf = asOf,
             leftBars = leftBars,
             rightBars = rightBars
         )
         .filter { it.isConfirmedAt(asOf) }
         .sortedBy { it.time.instant }

 if (confirmedSwings.isEmpty()) {
     return MarketStructure(
         instrument = series.instrument,
         timeframe = series.timeframe,
         asOf = asOf,
         points = emptyList(),
         direction = StructureDirection.UNKNOWN
     )
 }

 val structurePoints =
     classifySwings(confirmedSwings)

 return MarketStructure(
     instrument = series.instrument,
     timeframe = series.timeframe,
     asOf = asOf,
     points = structurePoints,
     direction = determineDirection(structurePoints)
 )
  
  }
  
  private fun classifySwings(
  swings: List<SwingPoint>
  ): List<StructurePoint> {
  
   val lastHighs = mutableListOf<SwingPoint>()
 val lastLows = mutableListOf<SwingPoint>()

 return swings.map { swing ->

     val classification =
         when (swing.type) {

             SwingPointType.HIGH -> {
                 val previousHigh =
                     lastHighs.lastOrNull()

                 lastHighs += swing

                 when {
                     previousHigh == null ->
                         StructureClassification.UNCLASSIFIED

                     swing.price > previousHigh.price ->
                         StructureClassification.HIGHER_HIGH

                     swing.price < previousHigh.price ->
                         StructureClassification.LOWER_HIGH

                     else ->
                         StructureClassification.UNCLASSIFIED
                 }
             }

             SwingPointType.LOW -> {
                 val previousLow =
                     lastLows.lastOrNull()

                 lastLows += swing

                 when {
                     previousLow == null ->
                         StructureClassification.UNCLASSIFIED

                     swing.price > previousLow.price ->
                         StructureClassification.HIGHER_LOW

                     swing.price < previousLow.price ->
                         StructureClassification.LOWER_LOW

                     else ->
                         StructureClassification.UNCLASSIFIED
                 }
             }
         }

     StructurePoint(
         swing = swing,
         classification = classification
     )
 }
  
  }
  
  private fun determineDirection(
  points: List<StructurePoint>
  ): StructureDirection {
  
   val recentPoints =
     points
         .filter {
             it.classification != StructureClassification.UNCLASSIFIED
         }
         .takeLast(6)

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
