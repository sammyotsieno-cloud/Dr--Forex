package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* Represents a three-candle price imbalance commonly referred to as

* a Fair Value Gap (FVG).

* 

* The gap is represented as a price zone rather than a single line.

* It is an observed market structure feature and is not itself a

* trading signal.
  */
  data class FairValueGap(
  val type: FairValueGapType,
  val lowerPrice: Double,
  val upperPrice: Double,
  val originTime: MarketTime,
  val confirmationTime: MarketTime,
  val displacement: Double,
  val filledFraction: Double = 0.0,
  val isFilled: Boolean = false
  ) {
  
  init {
  require(lowerPrice.isFinite()) {
  "Lower FVG price must be finite."
  }
  
   require(upperPrice.isFinite()) {
     "Upper FVG price must be finite."
 }

 require(lowerPrice < upperPrice) {
     "FVG lower price must be below upper price."
 }

 require(displacement.isFinite() && displacement >= 0.0) {
     "FVG displacement must be finite and non-negative."
 }

 require(filledFraction in 0.0..1.0) {
     "Filled fraction must be between 0 and 1."
 }

 require(
     !confirmationTime.isBefore(originTime)
 ) {
     "FVG confirmation cannot occur before its origin."
 }

 if (isFilled) {
     require(filledFraction >= 1.0) {
         "A filled FVG must have a filled fraction of 1."
     }
 }
  
  }
  
  /**
  
  * Returns true when the FVG was already observable at [asOf].
    */
    fun isKnownAt(asOf: MarketTime): Boolean =
    confirmationTime.isAtOrBefore(asOf)
  
  /**
  
  * Returns true when a price lies inside the imbalance zone.
    */
    fun contains(price: Double): Boolean =
    price >= lowerPrice && price <= upperPrice
    }

/**

* Direction of the imbalance.
  */
  enum class FairValueGapType {
  
  BULLISH,
  
  BEARISH
  }
