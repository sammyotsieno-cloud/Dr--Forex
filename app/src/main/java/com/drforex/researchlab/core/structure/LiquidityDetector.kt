package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketCandle
import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime
import kotlin.math.abs

/**

* Detects observable liquidity concentrations and liquidity

* interactions from chronological market data.

* 

* This implementation intentionally separates:

* 

* 1. liquidity location

* 2. liquidity interaction

* 3. confirmation of the interaction

* 

* No future candle is used before its close time.
  */
  class LiquidityDetector(
  private val swingPointDetector: SwingPointDetector = SwingPointDetector()
  ) {
  
  fun detect(
  series: MarketSeries,
  asOf: MarketTime,
  leftBars: Int = 2,
  rightBars: Int = 2,
  equalLevelToleranceFraction: Double = 0.001
  ): List<LiquidityEvent> {
  
   require(equalLevelToleranceFraction >= 0.0) {
     "Liquidity tolerance cannot be negative."
 }

 val candles =
     series
         .availableAt(asOf)
         .sortedBy { it.closeTime.instant }

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

 if (swings.isEmpty()) {
     return emptyList()
 }

 val events = mutableListOf<LiquidityEvent>()

 detectEqualHighs(
     swings = swings,
     candles = candles,
     toleranceFraction = equalLevelToleranceFraction,
     output = events
 )

 detectEqualLows(
     swings = swings,
     candles = candles,
     toleranceFraction = equalLevelToleranceFraction,
     output = events
 )

 detectPreviousLevelSweeps(
     swings = swings,
     candles = candles,
     toleranceFraction = equalLevelToleranceFraction,
     output = events
 )

 return events
     .filter { it.isKnownAt(asOf) }
     .distinctBy {
         Triple(
             it.type,
             it.liquidityPrice,
             it.confirmationTime
         )
     }
     .sortedBy {
         it.confirmationTime.instant
     }
  
  }
  
  private fun detectEqualHighs(
  swings: List<SwingPoint>,
  candles: List<MarketCandle>,
  toleranceFraction: Double,
  output: MutableList<LiquidityEvent>
  ) {
  
   val highs =
     swings.filter {
         it.type == SwingPointType.HIGH
     }

 for (index in highs.indices) {

     val current = highs[index]

     for (previousIndex in 0 until index) {

         val previous = highs[previousIndex]

         if (
             !previous.isConfirmedAt(current.confirmationTime)
         ) {
             continue
         }

         val tolerance =
             maxOf(
                 abs(current.price),
                 abs(previous.price)
             ) * toleranceFraction

         if (
             abs(current.price - previous.price) <= tolerance
         ) {

             output += LiquidityEvent(
                 type = LiquidityEventType.EQUAL_HIGH_LIQUIDITY,
                 liquidityPrice =
                     (current.price + previous.price) / 2.0,
                 eventTime = current.time,
                 confirmationTime = current.confirmationTime,
                 sourceSwing = current,
                 magnitude = abs(
                     current.price - previous.price
                 )
             )

             break
         }
     }
 }
  
  }
  
  private fun detectEqualLows(
  swings: List<SwingPoint>,
  candles: List<MarketCandle>,
  toleranceFraction: Double,
  output: MutableList<LiquidityEvent>
  ) {
  
   val lows =
     swings.filter {
         it.type == SwingPointType.LOW
     }

 for (index in lows.indices) {

     val current = lows[index]

     for (previousIndex in 0 until index) {

         val previous = lows[previousIndex]

         if (
             !previous.isConfirmedAt(current.confirmationTime)
         ) {
             continue
         }

         val tolerance =
             maxOf(
                 abs(current.price),
                 abs(previous.price)
             ) * toleranceFraction

         if (
             abs(current.price - previous.price) <= tolerance
         ) {

             output += LiquidityEvent(
                 type = LiquidityEventType.EQUAL_LOW_LIQUIDITY,
                 liquidityPrice =
                     (current.price + previous.price) / 2.0,
                 eventTime = current.time,
                 confirmationTime = current.confirmationTime,
                 sourceSwing = current,
                 magnitude = abs(
                     current.price - previous.price
                 )
             )

             break
         }
     }
 }
  
  }
  
  private fun detectPreviousLevelSweeps(
  swings: List<SwingPoint>,
  candles: List<MarketCandle>,
  toleranceFraction: Double,
  output: MutableList<LiquidityEvent>
  ) {
  
   for (swing in swings) {

     val subsequentCandles =
         candles.filter {
             it.closeTime.isAfter(
                 swing.confirmationTime
             )
         }

     for (candle in subsequentCandles) {

         val tolerance =
             abs(swing.price) *
                 toleranceFraction

         when (swing.type) {

             SwingPointType.HIGH -> {

                 val swept =
                     candle.high >
                         swing.price + tolerance

                 val rejected =
                     candle.close <=
                         swing.price

                 if (swept && rejected) {

                     output += LiquidityEvent(
                         type =
                             LiquidityEventType.BUY_SIDE_SWEEP,
                         liquidityPrice =
                             swing.price,
                         eventTime =
                             candle.closeTime,
                         confirmationTime =
                             candle.closeTime,
                         sourceSwing = swing,
                         magnitude =
                             candle.high -
                                 swing.price
                     )
                 }
             }

             SwingPointType.LOW -> {

                 val swept =
                     candle.low <
                         swing.price - tolerance

                 val rejected =
                     candle.close >=
                         swing.price

                 if (swept && rejected) {

                     output += LiquidityEvent(
                         type =
                             LiquidityEventType.SELL_SIDE_SWEEP,
                         liquidityPrice =
                             swing.price,
                         eventTime =
                             candle.closeTime,
                         confirmationTime =
                             candle.closeTime,
                         sourceSwing = swing,
                         magnitude =
                             swing.price -
                                 candle.low
                     )
                 }
             }
         }
     }
 }
  
  }
  }
