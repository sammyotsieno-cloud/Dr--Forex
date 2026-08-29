package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Range, rolling-extreme, and price-location measurements.

* 

* These metrics describe the distribution of price inside a

* historical observation window. They do not generate trading

* signals.
  */
  object RangeMetrics {
  
  /**
  
  * Highest high over the requested period.
    */
    fun highestHigh(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Range period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    return candles
    .takeLast(period)
    .maxOfOrNull { it.high }
    }
  
  /**
  
  * Lowest low over the requested period.
    */
    fun lowestLow(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Range period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    return candles
    .takeLast(period)
    .minOfOrNull { it.low }
    }
  
  /**
  
  * Width of the rolling high-low range.
    */
    fun rangeWidth(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val high =
    highestHigh(
    candles = candles,
    period = period
    )
    ?: return null
    
    val low =
    lowestLow(
    candles = candles,
    period = period
    )
    ?: return null
    
    return high - low
    }
  
  /**
  
  * Location of the latest close within its rolling high-low range.
  
  * 
  
  * 0.0 = rolling low
  
  * 0.5 = midpoint
  
  * 1.0 = rolling high
    */
    fun normalizedRangePosition(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val high =
    highestHigh(
    candles = candles,
    period = period
    )
    ?: return null
    
    val low =
    lowestLow(
    candles = candles,
    period = period
    )
    ?: return null
    
    val width =
    high - low
    
    if (width <= 0.0) {
    return null
    }
    
    val close =
    candles.lastOrNull()?.close
    ?: return null
    
    return (
    close - low
    ) / width
    }
  
  /**
  
  * Distance from the latest close to the rolling high,
  
  * normalized by the rolling range.
    */
    fun distanceFromHighPercent(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val high =
    highestHigh(
    candles = candles,
    period = period
    )
    ?: return null
    
    val low =
    lowestLow(
    candles = candles,
    period = period
    )
    ?: return null
    
    val width =
    high - low
    
    if (width <= 0.0) {
    return null
    }
    
    val close =
    candles.lastOrNull()?.close
    ?: return null
    
    return (
    (high - close) /
    width
    ) * 100.0
    }
  
  /**
  
  * Distance from the latest close to the rolling low,
  
  * normalized by the rolling range.
    */
    fun distanceFromLowPercent(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val high =
    highestHigh(
    candles = candles,
    period = period
    )
    ?: return null
    
    val low =
    lowestLow(
    candles = candles,
    period = period
    )
    ?: return null
    
    val width =
    high - low
    
    if (width <= 0.0) {
    return null
    }
    
    val close =
    candles.lastOrNull()?.close
    ?: return null
    
    return (
    (close - low) /
    width
    ) * 100.0
    }
  
  /**
  
  * Midpoint of the rolling high-low range.
    */
    fun midpoint(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val high =
    highestHigh(
    candles = candles,
    period = period
    )
    ?: return null
    
    val low =
    lowestLow(
    candles = candles,
    period = period
    )
    ?: return null
    
    return (high + low) / 2.0
    }
  
  /**
  
  * Measures how much the current rolling range has expanded or
  
  * contracted relative to a preceding rolling range.
  
  * 
  
  * Positive values indicate expansion.
  
  * Negative values indicate contraction.
    */
    fun rangeExpansionRatio(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Range period must be greater than zero."
    }
    
    if (candles.size < period * 2) {
    return null
    }
    
    val currentRange =
    rangeWidth(
    candles = candles.takeLast(period),
    period = period
    )
    ?: return null
    
    val previousRange =
    rangeWidth(
    candles = candles.dropLast(period).takeLast(period),
    period = period
    )
    ?: return null
    
    if (previousRange <= 0.0) {
    return null
    }
    
    return currentRange / previousRange
    }
    }
