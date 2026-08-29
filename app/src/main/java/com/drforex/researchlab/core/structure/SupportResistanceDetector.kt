package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketCandle
import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime
import kotlin.math.abs
import kotlin.math.max

/**

* Detects candidate support and resistance zones from confirmed

* swing points and subsequent market reactions.

* 

* This detector is intentionally conservative:

* 

* - Only data available at [asOf] may be used.

* - Zones originate from confirmed swing points.

* - Zone width is volatility-aware when possible.

* - Reactions are counted only after the zone becomes observable.

* 

* The output represents research evidence, not trading signals.
  */
  class SupportResistanceDetector(
  private val swingPointDetector: SwingPointDetector = SwingPointDetector()
  ) {
  
  fun detect(
  series: MarketSeries,
  asOf: MarketTime,
  leftBars: Int = 2,
  rightBars: Int = 2,
  toleranceFraction: Double = 0.0015
  ): List<SupportResistanceZone> {
  
   require(toleranceFraction >= 0.0) {
     "Tolerance fraction cannot be negative."
 }

 val candles = series
     .availableAt(asOf)
     .sortedBy { it.closeTime.instant }

 if (candles.isEmpty()) {
     return emptyList()
 }

 val swings = swingPointDetector
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

 return swings
     .mapNotNull { swing ->

         val originCandle =
             candles.lastOrNull {
                 !it.closeTime.isAfter(swing.time)
             }

         val tolerance =
             calculateTolerance(
                 candles = candles,
                 originCandle = originCandle,
                 price = swing.price,
                 toleranceFraction = toleranceFraction
             )

         val lower =
             swing.price - tolerance

         val upper =
             swing.price + tolerance

         val reactionCount =
             countReactions(
                 candles = candles,
                 originTime = swing.confirmationTime,
                 lowerPrice = lower,
                 upperPrice = upper,
                 type = when (swing.type) {
                     SwingPointType.HIGH ->
                         SupportResistanceType.RESISTANCE

                     SwingPointType.LOW ->
                         SupportResistanceType.SUPPORT
                 }
             )

         SupportResistanceZone(
             type = when (swing.type) {
                 SwingPointType.HIGH ->
                     SupportResistanceType.RESISTANCE

                 SwingPointType.LOW ->
                     SupportResistanceType.SUPPORT
             },
             lowerPrice = lower,
             upperPrice = upper,
             originTime = swing.time,
             confirmationTime = swing.confirmationTime,
             reactionCount = max(1, reactionCount),
             strength = classifyStrength(reactionCount),
             source = SupportResistanceSource.SWING_POINT
         )
     }
     .sortedBy { it.originTime.instant }
     .distinctBy {
         Triple(
             it.type,
             it.lowerPrice,
             it.upperPrice
         )
     }
  
  }
  
  private fun calculateTolerance(
  candles: List<MarketCandle>,
  originCandle: MarketCandle?,
  price: Double,
  toleranceFraction: Double
  ): Double {
  
   val fractionTolerance =
     abs(price) * toleranceFraction

 val candleRange =
     originCandle?.let {
         abs(it.high - it.low)
     } ?: 0.0

 return max(
     fractionTolerance,
     candleRange * 0.25
 )
  
  }
  
  private fun countReactions(
  candles: List<MarketCandle>,
  originTime: MarketTime,
  lowerPrice: Double,
  upperPrice: Double,
  type: SupportResistanceType
  ): Int {
  
   var reactions = 0

 candles
     .filter {
         it.closeTime.isAfter(originTime)
     }
     .forEach { candle ->

         val touched =
             when (type) {

                 SupportResistanceType.SUPPORT ->
                     candle.low <= upperPrice &&
                         candle.close >= lowerPrice

                 SupportResistanceType.RESISTANCE ->
                     candle.high >= lowerPrice &&
                         candle.close <= upperPrice
             }

         if (touched) {
             reactions++
         }
     }

 return reactions
  
  }
  
  private fun classifyStrength(
  reactionCount: Int
  ): SupportResistanceStrength {
  
   return when {
     reactionCount >= 6 ->
         SupportResistanceStrength.MAJOR

     reactionCount >= 4 ->
         SupportResistanceStrength.STRONG

     reactionCount >= 2 ->
         SupportResistanceStrength.MODERATE

     else ->
         SupportResistanceStrength.WEAK
 }
  
  }
  }
