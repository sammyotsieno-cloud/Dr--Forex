package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle
import kotlin.math.abs

/**

* General momentum and price-change measurements.

* 

* These metrics describe how price has moved over a defined

* observation window. They do not generate trading signals.

* 

* All calculations are deterministic and operate only on the

* supplied historical observations.
  */
  object MomentumMetrics {
  
  /**
  
  * Raw price momentum:
  
  * 
  
  * current close - close [period] observations earlier.
    */
    fun momentum(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Momentum period must be greater than zero."
    }
    
    if (candles.size <= period) {
    return null
    }
    
    val current =
    candles.last().close
    
    val previous =
    candles[candles.lastIndex - period].close
    
    return current - previous
    }
  
  /**
  
  * Percentage rate of change.
  
  * 
  
  * ((current - previous) / previous) * 100
    */
    fun rateOfChangePercent(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "ROC period must be greater than zero."
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
  
  * Absolute percentage change.
    */
    fun absoluteChangePercent(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    return rateOfChangePercent(
    candles = candles,
    period = period
    )?.let { abs(it) }
    }
  
  /**
  
  * Directional momentum.
  
  * 
  
  * Positive values indicate upward price movement.
  
  * Negative values indicate downward price movement.
    */
    fun directionalMomentum(
    candles: List<MarketCandle>,
    period: Int
    ): MomentumDirection? {
    
    val value =
    momentum(
    candles = candles,
    period = period
    )
    ?: return null
    
    return when {
    value > 0.0 ->
    MomentumDirection.POSITIVE
    
     value < 0.0 ->
     MomentumDirection.NEGATIVE

 else ->
     MomentumDirection.NEUTRAL
    
    }
    }
  
  /**
  
  * Calculates momentum at a specific candle index.
  
  * 
  
  * Only observations through [index] are considered.
    */
    fun momentumAt(
    candles: List<MarketCandle>,
    period: Int,
    index: Int
    ): Double? {
    
    require(period > 0) {
    "Momentum period must be greater than zero."
    }
    
    require(index >= 0) {
    "Index cannot be negative."
    }
    
    if (index >= candles.size) {
    return null
    }
    
    if (index < period) {
    return null
    }
    
    val current =
    candles[index].close
    
    val previous =
    candles[index - period].close
    
    return current - previous
    }
  
  /**
  
  * Calculates ROC at a specific candle index.
  
  * 
  
  * This method is explicitly point-in-time safe.
    */
    fun rateOfChangePercentAt(
    candles: List<MarketCandle>,
    period: Int,
    index: Int
    ): Double? {
    
    require(period > 0) {
    "ROC period must be greater than zero."
    }
    
    require(index >= 0) {
    "Index cannot be negative."
    }
    
    if (index >= candles.size) {
    return null
    }
    
    if (index < period) {
    return null
    }
    
    val current =
    candles[index].close
    
    val previous =
    candles[index - period].close
    
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
    }

/**

* Direction of measured price momentum.
  */
  enum class MomentumDirection {
  
  POSITIVE,
  
  NEGATIVE,
  
  NEUTRAL
  }
