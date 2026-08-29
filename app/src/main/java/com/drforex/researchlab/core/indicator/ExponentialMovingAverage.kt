package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Exponential Moving Average calculations.

* 

* EMA gives greater weight to recent observations. The implementation

* uses an SMA seed once enough observations are available, then applies

* the standard recursive EMA calculation.

* 

* This class contains descriptive calculations only and does not

* generate trading signals.
  */
  object ExponentialMovingAverage {
  
  /**
  
  * Calculates the latest EMA value.
  
  * 
  
  * Returns null until [period] candles are available.
    */
    fun calculate(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "EMA period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    val multiplier =
    2.0 / (period + 1.0)
    
    var ema =
    candles
    .take(period)
    .map { it.close }
    .average()
    
    for (index in period until candles.size) {
    
     val price =
     candles[index].close

 ema =
     (
         (price - ema) * multiplier
     ) + ema
    
    }
    
    return ema
    }
  
  /**
  
  * Calculates the complete EMA series.
  
  * 
  
  * Entries before the warm-up period are null.
    */
    fun series(
    candles: List<MarketCandle>,
    period: Int
    ): List<Double?> {
    
    require(period > 0) {
    "EMA period must be greater than zero."
    }
    
    if (candles.isEmpty()) {
    return emptyList()
    }
    
    val result =
    MutableList<Double?>(
    candles.size
    ) { null }
    
    if (candles.size < period) {
    return result
    }
    
    val multiplier =
    2.0 / (period + 1.0)
    
    var ema =
    candles
    .take(period)
    .map { it.close }
    .average()
    
    result[period - 1] = ema
    
    for (index in period until candles.size) {
    
     val price =
     candles[index].close

 ema =
     (
         (price - ema) * multiplier
     ) + ema

 result[index] = ema
    
    }
    
    return result
    }
  
  /**
  
  * Calculates the EMA from a rolling window ending at the supplied
  
  * observation.
  
  * 
  
  * This helper is useful when a research component needs an EMA
  
  * without accidentally consuming candles beyond its evaluation
  
  * timestamp.
    */
    fun calculateAt(
    candles: List<MarketCandle>,
    period: Int,
    index: Int
    ): Double? {
    
    require(period > 0) {
    "EMA period must be greater than zero."
    }
    
    require(index >= 0) {
    "Index cannot be negative."
    }
    
    if (index >= candles.size) {
    return null
    }
    
    if (index + 1 < period) {
    return null
    }
    
    return calculate(
    candles = candles.subList(
    0,
    index + 1
    ),
    period = period
    )
    }
    }
