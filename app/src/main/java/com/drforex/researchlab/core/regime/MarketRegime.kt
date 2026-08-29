package com.drforex.researchlab.core.regime

import com.drforex.researchlab.core.time.MarketTime

/**

* Describes the broader statistical/structural environment in which

* the market is currently operating.

* 

* A regime is descriptive, not predictive. It must never be treated

* as a trading signal by itself.
  */
  data class MarketRegime(
  val observedAt: MarketTime,
  val classification: RegimeClassification,
  val confidence: Double,
  val evidence: List<RegimeEvidence> = emptyList()
  ) {
  
  init {
  require(confidence in 0.0..1.0) {
  "Regime confidence must be between 0.0 and 1.0."
  }
  }
  }

/**

* High-level market-environment classifications.
  */
  enum class RegimeClassification {
  
  TRENDING_UP,
  
  TRENDING_DOWN,
  
  RANGING,
  
  HIGH_VOLATILITY,
  
  LOW_VOLATILITY,
  
  TRANSITION,
  
  UNCERTAIN
  }

/**

* Evidence supporting a regime classification.

* 

* Keeping evidence separate allows later research to inspect why

* a regime was classified in a particular way.
  */
  data class RegimeEvidence(
  val source: String,
  val description: String,
  val weight: Double = 1.0
  ) {
  
  init {
  require(weight >= 0.0) {
  "Evidence weight cannot be negative."
  }
  }
  }
