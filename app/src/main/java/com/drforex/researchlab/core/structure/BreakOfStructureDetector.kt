package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Detects confirmed Break of Structure events using only information

* available at [asOf].

* 

* A break is considered confirmed when a candle closes beyond the

* relevant confirmed swing level.
  */
  class BreakOfStructureDetector(
  private val swingPointDetector: SwingPointDetector = SwingPointDetector()
  ) {
  
  fun detect(
  series: MarketSeries,
  asOf: MarketTime,
  leftBars: Int = 2,
  rightBars: Int = 2
  ): List<BreakOfStructure> {
  
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

 if (swings.isEmpty()) {
     return emptyList()
 }

 val events = mutableListOf<BreakOfStructure>()

 swings.forEach { swing ->

     val breakIndex =
         candles.indexOfFirst { candle ->

             if (candle.closeTime.isBefore(swing.confirmationTime)) {
                 false
             } else {
                 when (swing.type) {

                     SwingPointType.HIGH ->
                         candle.close > swing.price

                     SwingPointType.LOW ->
                         candle.close < swing.price
                 }
             }
         }

     if (breakIndex < 0) {
         return@forEach
     }

     val breakCandle = candles[breakIndex]

     val direction =
         when (swing.type) {
             SwingPointType.HIGH ->
                 BreakDirection.BULLISH

             SwingPointType.LOW ->
                 BreakDirection.BEARISH
         }

     val strength =
         classifyStrength(
             swing = swing,
             breakCandle = breakCandle
         )

     events += BreakOfStructure(
         brokenPoint = swing,
         breakTime = breakCandle.closeTime,
         confirmationTime = breakCandle.closeTime,
         direction = direction,
         breakPrice = breakCandle.close,
         strength = strength
     )
 }

 return events
     .distinctBy {
         Triple(
             it.brokenPoint.time,
             it.direction,
             it.breakTime
         )
     }
     .sortedBy {
         it.breakTime.instant
     }
  
  }
  
  private fun classifyStrength(
  swing: SwingPoint,
  breakCandle: com.drforex.researchlab.core.market.MarketCandle
  ): BreakStrength {
  
   val distance =
     when (swing.type) {
         SwingPointType.HIGH ->
             breakCandle.close - swing.price

         SwingPointType.LOW ->
             swing.price - breakCandle.close
     }

 return when {
     distance > 0.0 ->
         BreakStrength.NORMAL

     else ->
         BreakStrength.WEAK
 }
  
  }
  }
