package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketCandle
import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime
import kotlin.math.abs

/**

* Detects decisive price movements relative to a rolling historical

* range baseline.

* 

* The detector is point-in-time safe: only candles available at

* [asOf] participate in the calculation.

* 

* Displacement is market evidence, not a trading signal.
  */
  class DisplacementDetector {
  
  fun detect(
  series: MarketSeries,
  asOf: MarketTime,
  lookback: Int = 10,
  minimumStrengthRatio: Double = 1.5
  ): List<Displacement> {
  
   require(lookback >= 2) {
     "Lookback must be at least 2."
 }

 require(minimumStrengthRatio > 0.0) {
     "Minimum strength ratio must be greater than zero."
 }

 val candles =
     series
         .availableAt(asOf)
         .sortedBy { it.closeTime.instant }

 if (candles.size <= lookback) {
     return emptyList()
 }

 val events = mutableListOf<Displacement>()

 for (index in lookback until candles.size) {

     val candle =
         candles[index]

     val previous =
         candles.subList(
             index - lookback,
             index
         )

     val ranges =
         previous
             .map {
                 abs(it.high - it.low)
             }
             .filter { it > 0.0 }

     if (ranges.isEmpty()) {
         continue
     }

     val baselineRange =
         ranges.average()

     if (baselineRange <= 0.0) {
         continue
     }

     val currentRange =
         abs(candle.high - candle.low)

     val strengthRatio =
         currentRange / baselineRange

     if (strengthRatio < minimumStrengthRatio) {
         continue
     }

     val direction =
         when {
             candle.close > candle.open ->
                 DisplacementDirection.BULLISH

             candle.close < candle.open ->
                 DisplacementDirection.BEARISH

             else ->
                 continue
         }

     events += Displacement(
         direction = direction,
         startTime = candle.openTime,
         confirmationTime = candle.closeTime,
         startPrice = candle.open,
         endPrice = candle.close,
         range = currentRange,
         baselineRange = baselineRange,
         strengthRatio = strengthRatio,
         candleCount = 1
     )
 }

 return events
     .filter { it.isKnownAt(asOf) }
     .sortedBy {
         it.confirmationTime.instant
     }
  
  }
  }
