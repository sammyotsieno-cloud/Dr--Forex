package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Central calculation engine for quantitative market indicators.

* 

* IndicatorEngine coordinates the lower-level indicator utilities

* and exposes a consistent interface to higher-level research

* components.

* 

* This engine calculates descriptive market features only.

* It does not generate trading signals or make trading decisions.
  */
  class IndicatorEngine(
  private val defaultPeriods: List<Int> = listOf(
  5,
  10,
  14,
  20,
  50
  )
  ) {
  
  init {
  require(defaultPeriods.isNotEmpty()) {
  "At least one default period is required."
  }
  
   require(
     defaultPeriods.all { it > 0 }
 ) {
     "Indicator periods must be greater than zero."
 }
  
  }
  
  /**
  
  * Calculates a complete feature snapshot using the latest
  
  * available candle.
    */
    fun calculate(
    candles: List<MarketCandle>,
    periods: List<Int> = defaultPeriods
    ): IndicatorFeatureSet? {
    
    validatePeriods(periods)
    
    return IndicatorFeatureSet.calculate(
    candles = candles,
    periods = periods
    )
    }
  
  /**
  
  * Calculates a feature snapshot at a specific historical index.
  
  * 
  
  * No candle after [index] is available to the calculation.
    */
    fun calculateAt(
    candles: List<MarketCandle>,
    index: Int,
    periods: List<Int> = defaultPeriods
    ): IndicatorFeatureSet? {
    
    require(index >= 0) {
    "Index cannot be negative."
    }
    
    validatePeriods(periods)
    
    return IndicatorFeatureSet.calculateAt(
    candles = candles,
    index = index,
    periods = periods
    )
    }
  
  /**
  
  * Calculates a feature snapshot at the latest candle while
  
  * explicitly requiring at least [minimumCandles] observations.
  
  * 
  
  * This provides a simple guard against accidentally requesting
  
  * a research feature set before enough market history exists.
    */
    fun calculateWithMinimumHistory(
    candles: List<MarketCandle>,
    minimumCandles: Int,
    periods: List<Int> = defaultPeriods
    ): IndicatorFeatureSet? {
    
    require(minimumCandles > 0) {
    "Minimum history must be greater than zero."
    }
    
    if (candles.size < minimumCandles) {
    return null
    }
    
    return calculate(
    candles = candles,
    periods = periods
    )
    }
  
  /**
  
  * Calculates one named feature family at a specific historical
  
  * point.
  
  * 
  
  * This method is intentionally limited to existing feature
  
  * families so higher-level systems do not need to know the
  
  * implementation details of individual calculators.
    */
    fun calculateFamilyAt(
    candles: List<MarketCandle>,
    index: Int,
    family: IndicatorFamily,
    period: Int
    ): Double? {
    
    require(index >= 0) {
    "Index cannot be negative."
    }
    
    require(period > 0) {
    "Indicator period must be greater than zero."
    }
    
    if (index >= candles.size) {
    return null
    }
    
    val history =
    candles.subList(
    0,
    index + 1
    )
    
    return when (family) {
    
     IndicatorFamily.SMA ->
     MovingAverage.sma(
         candles = history,
         period = period
     )

 IndicatorFamily.EMA ->
     ExponentialMovingAverage.calculate(
         candles = history,
         period = period
     )

 IndicatorFamily.ATR ->
     AverageTrueRange.calculate(
         candles = history,
         period = period
     )

 IndicatorFamily.RSI ->
     RelativeStrengthIndex.calculate(
         candles = history,
         period = period
     )

 IndicatorFamily.MOMENTUM ->
     MomentumMetrics.momentum(
         candles = history,
         period = period
     )

 IndicatorFamily.ROC_PERCENT ->
     MomentumMetrics.rateOfChangePercent(
         candles = history,
         period = period
     )

 IndicatorFamily.RETURN_STD_DEV ->
     VolatilityMetrics.returnStandardDeviation(
         candles = history,
         period = period
     )

 IndicatorFamily.AVERAGE_RANGE ->
     VolatilityMetrics.averageRange(
         candles = history,
         period = period
     )

 IndicatorFamily.NORMALIZED_ATR ->
     VolatilityMetrics.normalizedAtrPercent(
         candles = history,
         period = period
     )

 IndicatorFamily.RANGE_WIDTH ->
     RangeMetrics.rangeWidth(
         candles = history,
         period = period
     )

 IndicatorFamily.RANGE_POSITION ->
     RangeMetrics.normalizedRangePosition(
         candles = history,
         period = period
     )

 IndicatorFamily.RELATIVE_VOLUME ->
     VolumeMetrics.relativeVolume(
         candles = history,
         period = period
     )
    
    }
    }
  
  private fun validatePeriods(
  periods: List<Int>
  ) {
  
   require(periods.isNotEmpty()) {
     "At least one indicator period is required."
 }

 require(
     periods.all { it > 0 }
 ) {
     "Indicator periods must be greater than zero."
 }
  
  }
  }

/**

* Supported quantitative indicator families exposed through the

* central IndicatorEngine.
  */
  enum class IndicatorFamily {
  
  SMA,
  
  EMA,
  
  ATR,
  
  RSI,
  
  MOMENTUM,
  
  ROC_PERCENT,
  
  RETURN_STD_DEV,
  
  AVERAGE_RANGE,
  
  NORMALIZED_ATR,
  
  RANGE_WIDTH,
  
  RANGE_POSITION,
  
  RELATIVE_VOLUME
  }
