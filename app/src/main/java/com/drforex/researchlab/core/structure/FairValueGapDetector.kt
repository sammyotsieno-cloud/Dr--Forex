package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketCandle
import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime
import kotlin.math.abs

/**

* Detects three-candle price imbalances (Fair Value Gaps).

* 

* Only candles available at [asOf] are considered. An FVG becomes

* observable only after the third candle has closed.

* 

* The detector reports market observations; it does not generate

* trading signals.
  */
  class FairValueGapDetector {
  
  fun detect(
  series: MarketSeries,
  asOf: MarketTime,
  minimumGapFraction: Double = 0.0
  ): List<FairValueGap> {
  
   require(minimumGapFraction >= 0.0) {
     "Minimum gap fraction cannot be negative."
 }

 val candles =
     series
         .availableAt(asOf)
         .sortedBy { it.closeTime.instant }

 if (candles.size < 3) {
     return emptyList()
 }

 val gaps = mutableListOf<FairValueGap>()

 for (index in 2 until candles.size) {

     val first = candles[index - 2]
     val middle = candles[index - 1]
     val third = candles[index]

     val bullishGap =
         third.low > first.high

     val bearishGap =
         third.high < first.low

     when {
         bullishGap -> {

             val lower = first.high
             val upper = third.low

             val gapSize = upper - lower

             if (
                 gapSize >=
                 abs(middle.close) *
                 minimumGapFraction
             ) {

                 gaps += FairValueGap(
                     type = FairValueGapType.BULLISH,
                     lowerPrice = lower,
                     upperPrice = upper,
                     originTime = middle.openTime,
                     confirmationTime = third.closeTime,
                     displacement = gapSize
                 )
             }
         }

         bearishGap -> {

             val lower = third.high
             val upper = first.low

             val gapSize = upper - lower

             if (
                 gapSize >=
                 abs(middle.close) *
                 minimumGapFraction
             ) {

                 gaps += FairValueGap(
                     type = FairValueGapType.BEARISH,
                     lowerPrice = lower,
                     upperPrice = upper,
                     originTime = middle.openTime,
                     confirmationTime = third.closeTime,
                     displacement = gapSize
                 )
             }
         }
     }
 }

 return gaps
     .filter { it.isKnownAt(asOf) }
     .sortedBy { it.confirmationTime.instant }
     .distinctBy {
         Triple(
             it.type,
             it.lowerPrice,
             it.upperPrice
         )
     }
  
  }
  }
