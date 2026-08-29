package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Detects structural transitions using confirmed market structure.

* 

* CHoCH is generated only when a previously established structural

* direction is contradicted by a confirmed break of the opposing

* structural level.

* 

* This detector does not generate trading signals.
  */
  class ChangeOfCharacterDetector(
  private val swingPointDetector: SwingPointDetector = SwingPointDetector()
  ) {
  
  fun detect(
  series: MarketSeries,
  asOf: MarketTime,
  leftBars: Int = 2,
  rightBars: Int = 2
  ): List<ChangeOfCharacter> {
  
   val candles = series.availableAt(asOf)

 if (candles.isEmpty()) {
     return emptyList()
 }

 val swings =
     swingPointDetector
         .detect(
             series = series,
             asOf = asOf,
             leftBars = leftBars,
             rightBars = rightBars
         )
         .filter { it.isConfirmedAt(asOf) }
         .sortedBy { it.time.instant }

 if (swings.size < 3) {
     return emptyList()
 }

 val events = mutableListOf<ChangeOfCharacter>()

 var previousDirection = StructureDirection.UNKNOWN

 val classified =
     classifySwings(swings)

 for (index in classified.indices) {

     val current = classified[index]

     val currentDirection =
         directionAfterPoint(
             points = classified,
             index = index
         )

     if (
         previousDirection != StructureDirection.UNKNOWN &&
         currentDirection != StructureDirection.UNKNOWN &&
         currentDirection != previousDirection
     ) {

         val breakIndex =
             candles.indexOfFirst { candle ->

                 if (
                     candle.closeTime
                         .isBefore(current.swing.confirmationTime)
                 ) {
                     false
                 } else {
                     when (current.swing.type) {

                         SwingPointType.HIGH ->
                             candle.close > current.swing.price

                         SwingPointType.LOW ->
                             candle.close < current.swing.price
                     }
                 }
             }

         if (breakIndex >= 0) {

             val breakCandle = candles[breakIndex]

             events += ChangeOfCharacter(
                 brokenPoint = current.swing,
                 eventTime = breakCandle.closeTime,
                 confirmationTime = breakCandle.closeTime,
                 previousDirection = previousDirection,
                 newDirection = currentDirection,
                 breakPrice = breakCandle.close
             )
         }
     }

     if (currentDirection != StructureDirection.UNKNOWN) {
         previousDirection = currentDirection
     }
 }

 return events
     .distinctBy {
         Triple(
             it.brokenPoint.time,
             it.previousDirection,
             it.newDirection
         )
     }
     .sortedBy {
         it.confirmationTime.instant
     }
  
  }
  
  private fun classifySwings(
  swings: List<SwingPoint>
  ): List<StructurePoint> {
  
   val previousHighs = mutableListOf<SwingPoint>()
 val previousLows = mutableListOf<SwingPoint>()

 return swings.map { swing ->

     val classification =
         when (swing.type) {

             SwingPointType.HIGH -> {

                 val previous =
                     previousHighs.lastOrNull()

                 previousHighs += swing

                 when {
                     previous == null ->
                         StructureClassification.UNCLASSIFIED

                     swing.price > previous.price ->
                         StructureClassification.HIGHER_HIGH

                     swing.price < previous.price ->
                         StructureClassification.LOWER_HIGH

                     else ->
                         StructureClassification.UNCLASSIFIED
                 }
             }

             SwingPointType.LOW -> {

                 val previous =
                     previousLows.lastOrNull()

                 previousLows += swing

                 when {
                     previous == null ->
                         StructureClassification.UNCLASSIFIED

                     swing.price > previous.price ->
                         StructureClassification.HIGHER_LOW

                     swing.price < previous.price ->
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
  
  private fun directionAfterPoint(
  points: List<StructurePoint>,
  index: Int
  ): StructureDirection {
  
   val recent =
     points
         .subList(0, index + 1)
         .filter {
             it.classification !=
                 StructureClassification.UNCLASSIFIED
         }
         .takeLast(4)

 if (recent.isEmpty()) {
     return StructureDirection.UNKNOWN
 }

 val bullish =
     recent.count {
         it.classification ==
             StructureClassification.HIGHER_HIGH ||
             it.classification ==
             StructureClassification.HIGHER_LOW
     }

 val bearish =
     recent.count {
         it.classification ==
             StructureClassification.LOWER_HIGH ||
             it.classification ==
             StructureClassification.LOWER_LOW
     }

 return when {
     bullish >= 2 && bullish > bearish ->
         StructureDirection.BULLISH

     bearish >= 2 && bearish > bullish ->
         StructureDirection.BEARISH

     bullish > 0 && bearish > 0 ->
         StructureDirection.TRANSITION

     else ->
         StructureDirection.UNKNOWN
 }
  
  }
  }
