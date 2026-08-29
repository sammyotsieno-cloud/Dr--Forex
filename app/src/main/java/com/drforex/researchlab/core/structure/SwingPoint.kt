package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* A confirmed local turning point in market price.

* 

* Swing points are foundational structural observations used by

* market-structure, support/resistance, liquidity and strategy

* research components.

* 

* Confirmation is explicit because a swing cannot be considered

* confirmed using candles that were not yet available at the

* observation time.
  */
  data class SwingPoint(
  val time: MarketTime,
  val price: Double,
  val type: SwingPointType,
  val confirmationTime: MarketTime,
  val strength: Int = 1
  ) {
  
  init {
  require(price.isFinite()) {
  "Swing price must be finite."
  }
  
   require(strength >= 1) {
     "Swing strength must be at least 1."
 }

 require(!confirmationTime.isBefore(time)) {
     "Swing confirmation cannot occur before the swing itself."
 }
  
  }
  
  /**
  
  * True when the swing was already confirmed at [asOf].
    */
    fun isConfirmedAt(asOf: MarketTime): Boolean =
    confirmationTime.isAtOrBefore(asOf)
    }

/**

* Direction of the confirmed swing.
  */
  enum class SwingPointType {
  
  HIGH,
  
  LOW
  }
