package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* Represents a confirmed structural change in market behavior.

* 

* CHoCH is treated as an observable structural event, not as a

* directional trading signal.

* 

* The event records the previous structural direction and the

* direction suggested by the newly broken structural level.
  */
  data class ChangeOfCharacter(
  val brokenPoint: SwingPoint,
  val eventTime: MarketTime,
  val confirmationTime: MarketTime,
  val previousDirection: StructureDirection,
  val newDirection: StructureDirection,
  val breakPrice: Double
  ) {
  
  init {
  require(breakPrice.isFinite()) {
  "CHoCH break price must be finite."
  }
  
   require(previousDirection != StructureDirection.UNKNOWN) {
     "Previous structure direction must be known."
 }

 require(newDirection != StructureDirection.UNKNOWN) {
     "New structure direction must be known."
 }

 require(previousDirection != newDirection) {
     "CHoCH must represent a directional structural change."
 }

 require(!eventTime.isBefore(brokenPoint.confirmationTime)) {
     "CHoCH cannot occur before the broken swing is confirmed."
 }

 require(!confirmationTime.isBefore(eventTime)) {
     "CHoCH confirmation cannot occur before the event."
 }
  
  }
  
  fun isKnownAt(asOf: MarketTime): Boolean =
  confirmationTime.isAtOrBefore(asOf)
  }
