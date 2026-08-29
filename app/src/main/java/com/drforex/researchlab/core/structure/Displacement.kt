package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* Represents an unusually decisive price movement relative to the

* surrounding market context.

* 

* Displacement is an observation, not a trading signal.

* 

* The actual classification threshold is intentionally stored with

* the event so experiments can later compare different definitions.
  */
  data class Displacement(
  val direction: DisplacementDirection,
  val startTime: MarketTime,
  val confirmationTime: MarketTime,
  val startPrice: Double,
  val endPrice: Double,
  val range: Double,
  val baselineRange: Double,
  val strengthRatio: Double,
  val candleCount: Int
  ) {
  
  init {
  require(startPrice.isFinite()) {
  "Displacement start price must be finite."
  }
  
   require(endPrice.isFinite()) {
     "Displacement end price must be finite."
 }

 require(range.isFinite() && range >= 0.0) {
     "Displacement range must be finite and non-negative."
 }

 require(
     baselineRange.isFinite() && baselineRange > 0.0
 ) {
     "Baseline range must be finite and greater than zero."
 }

 require(
     strengthRatio.isFinite() && strengthRatio >= 0.0
 ) {
     "Displacement strength ratio must be finite and non-negative."
 }

 require(candleCount >= 1) {
     "Displacement must contain at least one candle."
 }

 require(
     !confirmationTime.isBefore(startTime)
 ) {
     "Displacement confirmation cannot occur before its start."
 }
  
  }
  
  /**
  
  * True when the displacement event was observable at [asOf].
    */
    fun isKnownAt(asOf: MarketTime): Boolean =
    confirmationTime.isAtOrBefore(asOf)
  
  /**
  
  * Price movement magnitude.
    */
    fun movement(): Double =
    kotlin.math.abs(endPrice - startPrice)
    }

/**

* Direction of the displacement.
  */
  enum class DisplacementDirection {
  
  BULLISH,
  
  BEARISH
  }
