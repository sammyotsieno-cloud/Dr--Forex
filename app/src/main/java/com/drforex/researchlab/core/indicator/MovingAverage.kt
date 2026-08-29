package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Moving-average calculation utilities.

* 

* These functions operate only on the supplied historical candles.

* They do not make trading decisions.
  */
  object MovingAverage {
  
  /**
  
  * Simple Moving Average.
  
  * 
  
  * Returns null until [period] observations are available.
    */
    fun sma(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "SMA period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    return candles
    .takeLast(period)
    .map { it.close }
    .average()
    }
  
  /**
  
  * Weighted Moving Average.
  
  * 
  
  * The newest observation receives the greatest weight.
    */
    fun wma(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "WMA period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    val window =
    candles.takeLast(period)
    
    var weightedSum = 0.0
    var weightSum = 0.0
    
    window.forEachIndexed { index, candle ->
    
     val weight =
     (index + 1).toDouble()

 weightedSum +=
     candle.close * weight

 weightSum += weight
    
    }
    
    return if (weightSum > 0.0) {
    weightedSum / weightSum
    } else {
    null
    }
    }
  
  /**
  
  * Returns the complete SMA series for the supplied candles.
  
  * 
  
  * Each value corresponds to the candle at the same index.
  
  * Values before the required warm-up period are null.
    */
    fun smaSeries(
    candles: List<MarketCandle>,
    period: Int
    ): List<Double?> {
    
    require(period > 0) {
    "SMA period must be greater than zero."
    }
    
    return candles.indices.map { index ->
    
     if (index + 1 < period) {
     null
 } else {
     sma(
         candles = candles.subList(
             0,
             index + 1
         ),
         period = period
     )
 }
    
    }
    }
  
  /**
  
  * Returns the complete WMA series for the supplied candles.
  
  * 
  
  * Values before the required warm-up period are null.
    */
    fun wmaSeries(
    candles: List<MarketCandle>,
    period: Int
    ): List<Double?> {
    
    require(period > 0) {
    "WMA period must be greater than zero."
    }
    
    return candles.indices.map { index ->
    
     if (index + 1 < period) {
     null
 } else {
     wma(
         candles = candles.subList(
             0,
             index + 1
         ),
         period = period
     )
 }
    
    }
    }
    }
