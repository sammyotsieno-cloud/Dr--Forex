package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* A confirmed structural break.

* 

* A Break of Structure represents price moving through a previously

* established structural level. It is an observation, not a trade

* signal.
  */
  data class BreakOfStructure(
  val brokenPoint: SwingPoint,
  val breakTime: MarketTime,
  val confirmationTime: MarketTime,
  val direction: BreakDirection,
  val breakPrice: Double,
  val strength: BreakStrength = BreakStrength.NORMAL
  ) {
  
  init {
  require(breakPrice.isFinite()) {
  "Break price must be finite."
  }
  
   require(!breakTime.isBefore(brokenPoint.confirmationTime)) {
     "A structural break cannot occur before the broken swing is confirmed."
 }

 require(!confirmationTime.isBefore(breakTime)) {
     "Break confirmation cannot occur before the break."
 }
  
  }
  
  fun isKnownAt(asOf: MarketTime): Boolean =
  confirmationTime.isAtOrBefore(asOf)
  }

/**

* Direction in which the structural level was broken.
  */
  enum class BreakDirection {
  
  BULLISH,
  
  BEARISH
  }

/**

* Qualitative classification of structural-break strength.
  */
  enum class BreakStrength {
  
  WEAK,
  
  NORMAL,
  
  STRONG
  }
