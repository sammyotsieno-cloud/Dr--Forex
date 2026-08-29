package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* Represents a point-in-time support or resistance zone.

* 

* A zone is an area of market acceptance/rejection rather than an

* arbitrary single price line.

* 

* The model intentionally stores the evidence used to construct the

* zone so that later research can evaluate whether a particular

* support/resistance definition actually provides predictive value.
  */
  data class SupportResistanceZone(
  val type: SupportResistanceType,
  val lowerPrice: Double,
  val upperPrice: Double,
  val originTime: MarketTime,
  val confirmationTime: MarketTime,
  val reactionCount: Int,
  val strength: SupportResistanceStrength,
  val source: SupportResistanceSource
  ) {
  
  init {
  require(lowerPrice.isFinite()) {
  "Lower price must be finite."
  }
  
   require(upperPrice.isFinite()) {
     "Upper price must be finite."
 }

 require(lowerPrice <= upperPrice) {
     "Lower price cannot exceed upper price."
 }

 require(reactionCount >= 1) {
     "A support/resistance zone must have at least one reaction."
 }

 require(
     !confirmationTime.isBefore(originTime)
 ) {
     "Confirmation cannot occur before zone origin."
 }
  
  }
  
  /**
  
  * Determines whether this zone is observable at a particular
  * point in research time.
    */
    fun isKnownAt(asOf: MarketTime): Boolean =
    confirmationTime.isAtOrBefore(asOf)
  
  /**
  
  * Determines whether a price lies inside the zone.
    */
    fun contains(price: Double): Boolean =
    price >= lowerPrice && price <= upperPrice
    }

/**

* Directional role of the zone.
  */
  enum class SupportResistanceType {
  
  SUPPORT,
  
  RESISTANCE
  }

/**

* Relative evidence strength assigned to the zone.

* 

* This is deliberately ordinal rather than a percentage. The exact

* scoring methodology can later become a research parameter.
  */
  enum class SupportResistanceStrength {
  
  WEAK,
  
  MODERATE,
  
  STRONG,
  
  MAJOR
  }

/**

* Evidence source from which the zone was derived.
  */
  enum class SupportResistanceSource {
  
  SWING_POINT,
  
  REPEATED_REACTION,
  
  STRUCTURAL_LEVEL,
  
  BREAK_AND_RETEST,
  
  MULTI_TIMEFRAME_CONFLUENCE
  }
