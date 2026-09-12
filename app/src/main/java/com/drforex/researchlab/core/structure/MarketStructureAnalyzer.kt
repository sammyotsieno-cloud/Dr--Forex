package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Converts confirmed swing points into a chronological market-structure

* representation.

* 

* The analyzer is responsible for discovering and classifying structural

* swing evidence from market data.

* 

* It does not:

* - generate trading signals

* - make trading decisions

* - evaluate profitability

* - infer future structure

* - expose information that was not confirmed by [asOf]

* 

* Structural direction is calculated by [MarketStructure] so that the

* direction semantics have one authoritative implementation shared by

* initial analysis and point-in-time projections.
  */
  class MarketStructureAnalyzer(
  private val swingPointDetector: SwingPointDetector = SwingPointDetector()
  ) {
  
  /**
  
  * Analyzes the market structure that was knowable at [asOf].
  
  * 
  
  * The temporal workflow is:
  
  * 
  
  * MarketSeries
  
  *  ↓
  
  * SwingPointDetector
  
  *  ↓
  
  * confirmed swings available at [asOf]
  
  *  ↓
  
  * chronological ordering
  
  *  ↓
  
  * structural classification
  
  *  ↓
  
  * MarketStructure
  
  * 
  
  * No future-confirmed swing is allowed into the resulting structure.
    */
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
    
    val structurePoints =
    classifySwings(confirmedSwings)
    
    return MarketStructure(
    instrument = series.instrument,
    timeframe = series.timeframe,
    asOf = asOf,
    points = structurePoints,
    direction = MarketStructure.determineDirection(structurePoints)
    )
    }
  
  /**
  
  * Classifies confirmed swings using only previously observed swings
  
  * of the same type.
  
  * 
  
  * Highs are compared with previous highs.
  
  * Lows are compared with previous lows.
  
  * 
  
  * This keeps classification chronological and prevents a later swing
  
  * from being used to classify an earlier swing.
    */
    private fun classifySwings(
    swings: List<SwingPoint>
    ): List<StructurePoint> {
    
    val previousHighs = mutableListOf<SwingPoint>()
    val previousLows = mutableListOf<SwingPoint>()
    
    return swings.map { swing ->
    
     val classification =
     when (swing.type) {

         SwingPointType.HIGH -> {
             val previousHigh =
                 previousHighs.lastOrNull()

             previousHighs += swing

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
                 previousLows.lastOrNull()

             previousLows += swing

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
  }
