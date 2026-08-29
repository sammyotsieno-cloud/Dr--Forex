package com.drforex.researchlab.core.time

/**

* Describes when information became available to the research system.

* 

* This is separate from when the underlying event actually occurred.

* The distinction is essential for point-in-time research and for

* preventing look-ahead bias.
  */
  data class InformationAvailability(
  val occurredAt: MarketTime,
  val availableAt: MarketTime,
  val recordedAt: MarketTime? = null
  ) {
  
  init {
  require(!availableAt.isBefore(occurredAt)) {
  "Information cannot become available before the event occurred."
  }
  
   recordedAt?.let {
     require(!it.isBefore(availableAt)) {
         "Information cannot be recorded before it becomes available."
     }
 }
  
  }
  
  /**
  
  * Returns true when the information was available at the supplied
  * point in time.
    */
    fun wasAvailableAt(time: MarketTime): Boolean =
    availableAt.isAtOrBefore(time)
  }
