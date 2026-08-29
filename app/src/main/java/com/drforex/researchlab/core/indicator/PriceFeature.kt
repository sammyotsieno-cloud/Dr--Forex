package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle
import kotlin.math.abs
import kotlin.math.ln

/**

* Deterministic price-derived measurements used by technical

* indicators and research features.

* 

* These functions do not make trading decisions.
  */
  object PriceFeature {
  
  /**
  
  * Full candle range.
    */
    fun range(candle: MarketCandle): Double =
    candle.high - candle.low
  
  /**
  
  * Absolute candle-body size.
    */
    fun body(candle: MarketCandle): Double =
    abs(candle.close - candle.open)
  
  /**
  
  * Upper wick size.
    */
    fun upperWick(candle: MarketCandle): Double =
    candle.high - maxOf(candle.open, candle.close)
  
  /**
  
  * Lower wick size.
    */
    fun lowerWick(candle: MarketCandle): Double =
    minOf(candle.open, candle.close) - candle.low
  
  /**
  
  * Body-to-range ratio.
  
  * 
  
  * Returns null for a zero-range candle.
    */
    fun bodyToRangeRatio(
    candle: MarketCandle
    ): Double? {
    
    val range = range(candle)
    
    if (range <= 0.0) {
    return null
    }
    
    return body(candle) / range
    }
  
  /**
  
  * Close location value.
  
  * 
  
  * -1 means the close is at the low.
  
  * 0 means the close is in the middle.
  
  * +1 means the close is at the high.
    */
    fun closeLocationValue(
    candle: MarketCandle
    ): Double? {
    
    val range = range(candle)
    
    if (range <= 0.0) {
    return null
    }
    
    return (
    ((candle.close - candle.low) / range) * 2.0
    ) - 1.0
    }
  
  /**
  
  * Signed candle return.
    */
    fun returnValue(
    previousClose: Double,
    currentClose: Double
    ): Double? {
    
    if (
    !previousClose.isFinite() ||
    !currentClose.isFinite() ||
    previousClose == 0.0
    ) {
    return null
    }
    
    return (
    currentClose - previousClose
    ) / previousClose
    }
  
  /**
  
  * Logarithmic return.
    */
    fun logReturn(
    previousClose: Double,
    currentClose: Double
    ): Double? {
    
    if (
    !previousClose.isFinite() ||
    !currentClose.isFinite() ||
    previousClose <= 0.0 ||
    currentClose <= 0.0
    ) {
    return null
    }
    
    return ln(
    currentClose / previousClose
    )
    }
  
  /**
  
  * True range using the previous closing price.
    */
    fun trueRange(
    candle: MarketCandle,
    previousClose: Double?
    ): Double {
    
    if (previousClose == null) {
    return range(candle)
    }
    
    return maxOf(
    candle.high - candle.low,
    abs(candle.high - previousClose),
    abs(candle.low - previousClose)
    )
    }
  
  /**
  
  * Candle direction.
    */
    fun direction(
    candle: MarketCandle
    ): PriceDirection =
    when {
    candle.close > candle.open ->
    PriceDirection.BULLISH
    
     candle.close < candle.open ->
     PriceDirection.BEARISH

 else ->
     PriceDirection.NEUTRAL
    
    }
    }

/**

* Direction of a price candle.
  */
  enum class PriceDirection {
  
  BULLISH,
  
  BEARISH,
  
  NEUTRAL
  }
