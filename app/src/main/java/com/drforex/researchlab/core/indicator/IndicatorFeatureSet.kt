package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Snapshot of quantitative market features calculated from a single

* point in time.

* 

* A feature may be null when the available history is insufficient

* for the requested calculation.

* 

* This object contains measurements only. It does not contain

* trading decisions, signals, entries, exits, or risk instructions.
  */
  data class IndicatorFeatureSet(
  val close: Double,
  val range: Double,
  val body: Double,
  val bodyToRangeRatio: Double?,
  val closeLocationValue: Double?,
  val returnPercent: Double?,
  val logReturn: Double?,
  val sma: Map<Int, Double?>,
  val ema: Map<Int, Double?>,
  val atr: Map<Int, Double?>,
  val rsi: Map<Int, Double?>,
  val momentum: Map<Int, Double?>,
  val rocPercent: Map<Int, Double?>,
  val normalizedAtrPercent: Map<Int, Double?>,
  val normalizedRangePosition: Map<Int, Double?>,
  val relativeVolume: Map<Int, Double?>
  ) {
  
  companion object {
  
   /**
  * Builds a feature snapshot from candles available through
  * the final supplied observation.
  */
 fun calculate(
     candles: List<MarketCandle>,
     periods: List<Int>
 ): IndicatorFeatureSet? {

     val latest =
         candles.lastOrNull()
             ?: return null

     require(
         periods.isNotEmpty()
     ) {
         "At least one feature period is required."
     }

     require(
         periods.all { it > 0 }
     ) {
         "Feature periods must be greater than zero."
     }

     val previousClose =
         candles
             .dropLast(1)
             .lastOrNull()
             ?.close

     return IndicatorFeatureSet(
         close = latest.close,

         range =
             PriceFeature.range(
                 latest
             ),

         body =
             PriceFeature.body(
                 latest
             ),

         bodyToRangeRatio =
             PriceFeature.bodyToRangeRatio(
                 latest
             ),

         closeLocationValue =
             PriceFeature.closeLocationValue(
                 latest
             ),

         returnPercent =
             previousClose?.let {
                 PriceFeature.returnValue(
                     previousClose = it,
                     currentClose = latest.close
                 )?.times(100.0)
             },

         logReturn =
             previousClose?.let {
                 PriceFeature.logReturn(
                     previousClose = it,
                     currentClose = latest.close
                 )
             },

         sma =
             periods.associateWith { period ->
                 MovingAverage.sma(
                     candles = candles,
                     period = period
                 )
             },

         ema =
             periods.associateWith { period ->
                 ExponentialMovingAverage.calculate(
                     candles = candles,
                     period = period
                 )
             },

         atr =
             periods.associateWith { period ->
                 AverageTrueRange.calculate(
                     candles = candles,
                     period = period
                 )
             },

         rsi =
             periods.associateWith { period ->
                 RelativeStrengthIndex.calculate(
                     candles = candles,
                     period = period
                 )
             },

         momentum =
             periods.associateWith { period ->
                 MomentumMetrics.momentum(
                     candles = candles,
                     period = period
                 )
             },

         rocPercent =
             periods.associateWith { period ->
                 MomentumMetrics.rateOfChangePercent(
                     candles = candles,
                     period = period
                 )
             },

         normalizedAtrPercent =
             periods.associateWith { period ->
                 VolatilityMetrics.normalizedAtrPercent(
                     candles = candles,
                     period = period
                 )
             },

         normalizedRangePosition =
             periods.associateWith { period ->
                 RangeMetrics.normalizedRangePosition(
                     candles = candles,
                     period = period
                 )
             },

         relativeVolume =
             periods.associateWith { period ->
                 VolumeMetrics.relativeVolume(
                     candles = candles,
                     period = period
                 )
             }
     )
 }

 /**
  * Builds a feature snapshot using candles through a specific
  * historical index.
  *
  * This is the preferred method for backtesting and historical
  * research because future candles are excluded.
  */
 fun calculateAt(
     candles: List<MarketCandle>,
     index: Int,
     periods: List<Int>
 ): IndicatorFeatureSet? {

     require(index >= 0) {
         "Index cannot be negative."
     }

     if (index >= candles.size) {
         return null
     }

     return calculate(
         candles = candles.subList(
             0,
             index + 1
         ),
         periods = periods
     )
 }
  
  }
  }
