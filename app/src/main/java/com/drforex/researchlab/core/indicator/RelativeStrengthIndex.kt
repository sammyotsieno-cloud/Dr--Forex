package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle
import kotlin.math.abs

/**

* Relative Strength Index (RSI).

* 

* RSI measures the magnitude of recent upward and downward price

* changes on a bounded 0-100 scale.

* 

* This implementation uses Wilder's smoothing method and exposes

* point-in-time calculations for historical research.

* 

* RSI is a descriptive feature and does not generate trading signals.
  */
  object RelativeStrengthIndex {
  
  /**
  
  * Calculates the latest RSI value.
  
  * 
  
  * Returns null when insufficient observations exist.
    */
    fun calculate(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "RSI period must be greater than zero."
    }
    
    if (candles.size <= period) {
    return null
    }
    
    val changes =
    priceChanges(candles)
    
    if (changes.size < period) {
    return null
    }
    
    var averageGain =
    changes
    .take(period)
    .map { maxOf(it, 0.0) }
    .average()
    
    var averageLoss =
    changes
    .take(period)
    .map { abs(minOf(it, 0.0)) }
    .average()
    
    for (index in period until changes.size) {
    
     val gain =
     maxOf(changes[index], 0.0)

 val loss =
     abs(minOf(changes[index], 0.0))

 averageGain =
     (
         (averageGain * (period - 1)) +
             gain
     ) / period

 averageLoss =
     (
         (averageLoss * (period - 1)) +
             loss
     ) / period
    
    }
    
    return rsiFromAverages(
    averageGain = averageGain,
    averageLoss = averageLoss
    )
    }
  
  /**
  
  * Calculates the complete RSI series.
  
  * 
  
  * Entries before the warm-up period are null.
    */
    fun series(
    candles: List<MarketCandle>,
    period: Int
    ): List<Double?> {
    
    require(period > 0) {
    "RSI period must be greater than zero."
    }
    
    val result =
    MutableList<Double?>(
    candles.size
    ) { null }
    
    if (candles.size <= period) {
    return result
    }
    
    val changes =
    priceChanges(candles)
    
    if (changes.size < period) {
    return result
    }
    
    var averageGain =
    changes
    .take(period)
    .map { maxOf(it, 0.0) }
    .average()
    
    var averageLoss =
    changes
    .take(period)
    .map { abs(minOf(it, 0.0)) }
    .average()
    
    result[period] =
    rsiFromAverages(
    averageGain,
    averageLoss
    )
    
    for (index in period until changes.size) {
    
     val gain =
     maxOf(changes[index], 0.0)

 val loss =
     abs(minOf(changes[index], 0.0))

 averageGain =
     (
         (averageGain * (period - 1)) +
             gain
     ) / period

 averageLoss =
     (
         (averageLoss * (period - 1)) +
             loss
     ) / period

 result[index + 1] =
     rsiFromAverages(
         averageGain,
         averageLoss
     )
    
    }
    
    return result
    }
  
  /**
  
  * Calculates RSI using only candles through [index].
  
  * 
  
  * This prevents future observations from entering a historical
  
  * feature calculation.
    */
    fun calculateAt(
    candles: List<MarketCandle>,
    period: Int,
    index: Int
    ): Double? {
    
    require(period > 0) {
    "RSI period must be greater than zero."
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
  
  private fun priceChanges(
  candles: List<MarketCandle>
  ): List<Double> {
  
   if (candles.size < 2) {
     return emptyList()
 }

 return candles
     .zipWithNext()
     .map { (previous, current) ->
         current.close - previous.close
     }
  
  }
  
  private fun rsiFromAverages(
  averageGain: Double,
  averageLoss: Double
  ): Double {
  
   if (averageLoss == 0.0) {
     return 100.0
 }

 if (averageGain == 0.0) {
     return 0.0
 }

 val relativeStrength =
     averageGain / averageLoss

 return 100.0 -
     (
         100.0 /
             (1.0 + relativeStrength)
         )
  
  }
  }
