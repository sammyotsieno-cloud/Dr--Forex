package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Quantitative trend measurements.

* 

* These metrics describe trend behaviour without declaring a trade

* direction or producing trading signals.
  */
  object TrendMetrics {
  
  /**
  
  * Difference between the current close and the close [period]
  
  * observations earlier.
    */
    fun priceSlope(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Trend period must be greater than zero."
    }
    
    if (candles.size <= period) {
    return null
    }
    
    return candles.last().close -
    candles[candles.lastIndex - period].close
    }
  
  /**
  
  * Percentage slope over [period] observations.
    */
    fun percentageSlope(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Trend period must be greater than zero."
    }
    
    if (candles.size <= period) {
    return null
    }
    
    val current =
    candles.last().close
    
    val previous =
    candles[candles.lastIndex - period].close
    
    if (
    !current.isFinite() ||
    !previous.isFinite() ||
    previous == 0.0
    ) {
    return null
    }
    
    return (
    (current - previous) /
    previous
    ) * 100.0
    }
  
  /**
  
  * Measures the position of the latest price relative to an SMA.
  
  * 
  
  * Positive values mean price is above the SMA.
  
  * Negative values mean price is below the SMA.
  
  * 
  
  * The result is normalized by the SMA value.
    */
    fun normalizedDistanceFromSma(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val sma =
    MovingAverage.sma(
    candles = candles,
    period = period
    )
    ?: return null
    
    val price =
    candles.lastOrNull()?.close
    ?: return null
    
    if (
    !price.isFinite() ||
    !sma.isFinite() ||
    sma == 0.0
    ) {
    return null
    }
    
    return (
    (price - sma) /
    sma
    ) * 100.0
    }
  
  /**
  
  * Measures the position of the latest price relative to an EMA.
    */
    fun normalizedDistanceFromEma(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val ema =
    ExponentialMovingAverage.calculate(
    candles = candles,
    period = period
    )
    ?: return null
    
    val price =
    candles.lastOrNull()?.close
    ?: return null
    
    if (
    !price.isFinite() ||
    !ema.isFinite() ||
    ema == 0.0
    ) {
    return null
    }
    
    return (
    (price - ema) /
    ema
    ) * 100.0
    }
  
  /**
  
  * Measures the fraction of consecutive candle-to-candle moves
  
  * in the same direction during the requested window.
  
  * 
  
  * A value near 1.0 indicates strong directional persistence.
  
  * A value near 0.5 indicates little directional persistence.
    */
    fun directionalPersistence(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Trend period must be greater than zero."
    }
    
    if (candles.size <= period) {
    return null
    }
    
    val start =
    candles.size - period
    
    var comparableMoves = 0
    var sameDirectionMoves = 0
    
    for (index in start + 1 until candles.size) {
    
     val previous =
     candles[index - 1].close

 val current =
     candles[index].close

 val previousMove =
     if (index - 1 > 0) {
         candles[index - 1].close -
             candles[index - 2].close
     } else {
         0.0
     }

 val currentMove =
     current - previous

 if (
     previousMove == 0.0 ||
     currentMove == 0.0
 ) {
     continue
 }

 comparableMoves++

 if (
     previousMove > 0.0 &&
     currentMove > 0.0
 ) {
     sameDirectionMoves++
 }

 if (
     previousMove < 0.0 &&
     currentMove < 0.0
 ) {
     sameDirectionMoves++
 }
    
    }
    
    if (comparableMoves == 0) {
    return null
    }
    
    return sameDirectionMoves.toDouble() /
    comparableMoves.toDouble()
    }
  
  /**
  
  * Classifies measured trend direction from the percentage slope.
    */
    fun direction(
    candles: List<MarketCandle>,
    period: Int
    ): TrendDirection? {
    
    val slope =
    percentageSlope(
    candles = candles,
    period = period
    )
    ?: return null
    
    return when {
    slope > 0.0 ->
    TrendDirection.UP
    
     slope < 0.0 ->
     TrendDirection.DOWN

 else ->
     TrendDirection.FLAT
    
    }
    }
    }

/**

* Descriptive trend direction.
  */
  enum class TrendDirection {
  
  UP,
  
  DOWN,
  
  FLAT
  }
