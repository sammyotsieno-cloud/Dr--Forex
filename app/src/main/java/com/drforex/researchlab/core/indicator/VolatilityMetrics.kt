package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle
import kotlin.math.sqrt

/**

* Quantitative volatility and dispersion measurements.

* 

* These metrics describe the magnitude and changing state of price

* movement. They do not produce trading signals.
  */
  object VolatilityMetrics {
  
  /**
  
  * Population standard deviation of percentage returns.
  
  * 
  
  * Returns null when fewer than two returns are available.
    */
    fun returnStandardDeviation(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Volatility period must be greater than zero."
    }
    
    if (candles.size <= period) {
    return null
    }
    
    val returns =
    percentageReturns(
    candles = candles.takeLast(period + 1)
    )
    
    if (returns.size < 2) {
    return null
    }
    
    return standardDeviation(returns)
    }
  
  /**
  
  * Annualization-free realized volatility estimate based on
  * percentage returns.
    */
    fun realizedVolatility(
    candles: List<MarketCandle>,
    period: Int
    ): Double? =
    returnStandardDeviation(
    candles = candles,
    period = period
    )
  
  /**
  
  * Average candle range over the requested period.
    */
    fun averageRange(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Volatility period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    return candles
    .takeLast(period)
    .map {
    PriceFeature.range(it)
    }
    .average()
    }
  
  /**
  
  * Average true range expressed as a percentage of current price.
  
  * 
  
  * This allows volatility comparisons across instruments with
  
  * different price scales.
    */
    fun normalizedAtrPercent(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val atr =
    AverageTrueRange.calculate(
    candles = candles,
    period = period
    )
    ?: return null
    
    val price =
    candles.lastOrNull()?.close
    ?: return null
    
    if (
    !price.isFinite() ||
    price == 0.0
    ) {
    return null
    }
    
    return (atr / price) * 100.0
    }
  
  /**
  
  * Ratio of the latest candle range to the average range over
  
  * the requested historical period.
  
  * 
  
  * Values above 1.0 indicate that the latest candle is larger
  
  * than its recent average.
    */
    fun rangeExpansionRatio(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val average =
    averageRange(
    candles = candles,
    period = period
    )
    ?: return null
    
    val latest =
    candles.lastOrNull()
    ?: return null
    
    val latestRange =
    PriceFeature.range(latest)
    
    if (
    average <= 0.0 ||
    !average.isFinite()
    ) {
    return null
    }
    
    return latestRange / average
    }
  
  /**
  
  * Measures whether the latest candle range is contracting or
  
  * expanding relative to the preceding average range.
  
  * 
  
  * Positive values indicate expansion.
  
  * Negative values indicate contraction.
    */
    fun rangeExpansionDelta(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Volatility period must be greater than zero."
    }
    
    if (candles.size < period + 1) {
    return null
    }
    
    val latest =
    PriceFeature.range(
    candles.last()
    )
    
    val previousAverage =
    candles
    .dropLast(1)
    .takeLast(period)
    .map {
    PriceFeature.range(it)
    }
    .average()
    
    if (previousAverage <= 0.0) {
    return null
    }
    
    return (
    latest - previousAverage
    ) / previousAverage
    }
  
  /**
  
  * Calculates the percentage return series used by volatility
  
  * measurements.
    */
    fun percentageReturns(
    candles: List<MarketCandle>
    ): List<Double> {
    
    if (candles.size < 2) {
    return emptyList()
    }
    
    return candles
    .zipWithNext()
    .mapNotNull { (previous, current) ->
    
         PriceFeature.returnValue(
         previousClose = previous.close,
         currentClose = current.close
     )?.times(100.0)
 }
  
  }
  
  /**
  
  * Standard deviation helper.
    */
    private fun standardDeviation(
    values: List<Double>
    ): Double {
    
    if (values.isEmpty()) {
    return 0.0
    }
    
    val mean =
    values.average()
    
    val variance =
    values
    .map {
    val difference =
    it - mean
    
             difference * difference
     }
     .average()
    
    return sqrt(variance)
    }
    }
