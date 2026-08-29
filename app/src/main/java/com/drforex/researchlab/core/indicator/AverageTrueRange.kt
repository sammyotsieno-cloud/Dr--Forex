package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Average True Range (ATR).

* 

* ATR measures market volatility rather than direction.

* 

* The implementation uses True Range observations and Wilder's

* smoothing method.

* 

* ATR is descriptive research data and does not generate signals.
  */
  object AverageTrueRange {
  
  /**
  
  * Calculates the latest ATR value.
  
  * 
  
  * The first ATR value is seeded from the average of the first
  
  * [period] true-range observations.
  
  * 
  
  * Returns null when insufficient observations exist.
    */
    fun calculate(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "ATR period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    val trueRanges =
    calculateTrueRanges(candles)
    
    if (trueRanges.size < period) {
    return null
    }
    
    var atr =
    trueRanges
    .take(period)
    .average()
    
    for (index in period until trueRanges.size) {
    
     atr =
     (
         (atr * (period - 1)) +
             trueRanges[index]
     ) / period
    
    }
    
    return atr
    }
  
  /**
  
  * Calculates the complete ATR series.
  
  * 
  
  * Values before the warm-up period are null.
    */
    fun series(
    candles: List<MarketCandle>,
    period: Int
    ): List<Double?> {
    
    require(period > 0) {
    "ATR period must be greater than zero."
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
    
    val trueRanges =
    calculateTrueRanges(candles)
    
    var atr =
    trueRanges
    .take(period)
    .average()
    
    result[period - 1] = atr
    
    for (index in period until trueRanges.size) {
    
     atr =
     (
         (atr * (period - 1)) +
             trueRanges[index]
     ) / period

 result[index] = atr
    
    }
    
    return result
    }
  
  /**
  
  * Calculates ATR at a specific candle index.
  
  * 
  
  * Only observations through [index] are considered.
    */
    fun calculateAt(
    candles: List<MarketCandle>,
    period: Int,
    index: Int
    ): Double? {
    
    require(period > 0) {
    "ATR period must be greater than zero."
    }
    
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
    period = period
    )
    }
  
  /**
  
  * Calculates true-range observations.
  
  * 
  
  * The first candle has no previous close, so its true range is
  
  * simply high minus low.
    */
    private fun calculateTrueRanges(
    candles: List<MarketCandle>
    ): List<Double> {
    
    if (candles.isEmpty()) {
    return emptyList()
    }
    
    val result =
    ArrayList<Double>(
    candles.size
    )
    
    for (index in candles.indices) {
    
     val candle =
     candles[index]

 val previousClose =
     if (index == 0) {
         null
     } else {
         candles[index - 1].close
     }

 result +=
     PriceFeature.trueRange(
         candle = candle,
         previousClose = previousClose
     )
    
    }
    
    return result
    }
    }
