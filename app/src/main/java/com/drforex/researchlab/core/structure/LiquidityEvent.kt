package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* Represents an observable interaction with a potential liquidity

* concentration.

* 

* A liquidity event is descriptive evidence only. It must never be

* interpreted as a trading signal by itself.
  */
  data class LiquidityEvent(
  val type: LiquidityEventType,
  val liquidityPrice: Double,
  val eventTime: MarketTime,
  val confirmationTime: MarketTime,
  val sourceSwing: SwingPoint? = null,
  val magnitude: Double = 0.0
  ) {
  
  init {
  require(liquidityPrice.isFinite()) {
  "Liquidity price must be finite."
  }
  
   require(magnitude >= 0.0) {
     "Liquidity magnitude cannot be negative."
 }

 require(
     !confirmationTime.isBefore(eventTime)
 ) {
     "Liquidity confirmation cannot occur before the event."
 }
  
  }
  
  /**
  
  * Determines whether this liquidity event was observable at [asOf].
    */
    fun isKnownAt(asOf: MarketTime): Boolean =
    confirmationTime.isAtOrBefore(asOf)
    }

/**

* Type of liquidity interaction observed in the market.
  */
  enum class LiquidityEventType {
  
  EQUAL_HIGH_LIQUIDITY,
  
  EQUAL_LOW_LIQUIDITY,
  
  PREVIOUS_HIGH_LIQUIDITY,
  
  PREVIOUS_LOW_LIQUIDITY,
  
  BUY_SIDE_SWEEP,
  
  SELL_SIDE_SWEEP,
  
  LIQUIDITY_GRAB,
  
  LIQUIDITY_REJECTION
  }
