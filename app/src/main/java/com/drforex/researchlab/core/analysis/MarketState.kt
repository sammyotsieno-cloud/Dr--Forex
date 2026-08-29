package com.drforex.researchlab.core.analysis

import com.drforex.researchlab.core.market.MarketInstrument
import com.drforex.researchlab.core.market.MarketTimeframe
import com.drforex.researchlab.core.time.MarketTime

/**

* Point-in-time representation of the market's observable state.

* 

* MarketState is a research object, not a trading signal.

* It describes what the analytical layer has established at a

* particular moment without deciding whether a trade should be taken.
  */
  data class MarketState(
  val instrument: MarketInstrument,
  val timeframe: MarketTimeframe,
  val asOf: MarketTime,
  val observations: List<TechnicalObservation>,
  val quality: MarketStateQuality = MarketStateQuality.COMPLETE
  ) {
  
  /**
  
  * Observations of a particular technical type.
    */
    fun observationsOf(
    type: TechnicalObservationType
    ): List<TechnicalObservation> =
    observations.filter { it.type == type }
  
  /**
  
  * Whether this state contains at least one observation of the
  * requested capability.
    */
    fun contains(
    type: TechnicalObservationType
    ): Boolean =
    observations.any { it.type == type }
  
  /**
  
  * Retrieve a numeric observation when one exists.
    */
    fun numericValues(
    type: TechnicalObservationType
    ): List<Double> =
    observationsOf(type)
    .mapNotNull { it.value }
    }

/**

* Completeness of the technical state at the requested point in time.
  */
  enum class MarketStateQuality {
  COMPLETE,
  PARTIAL,
  UNAVAILABLE
  }
