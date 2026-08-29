package com.drforex.researchlab.core.analysis

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Contract for deterministic technical analysis.

* 

* Implementations may calculate indicators, identify market structure,

* detect price zones, classify regimes, or derive other technical

* observations from validated market data.

* 

* The engine must never use information that was unavailable at the

* requested analysis time.
  */
  interface TechnicalAnalysisEngine {
  
  /**
  
  * Produces technical observations that were knowable at [asOf].
    */
    fun analyze(
    series: MarketSeries,
    asOf: MarketTime
    ): List<TechnicalObservation>
    }

/**

* Identifies a specific analytical capability.

* 

* Keeping capabilities explicit allows DrForex to compose multiple

* analytical engines without coupling the research system to one

* implementation.
  */
  interface TechnicalAnalyzer {
  
  val capability: TechnicalCapability
  
  fun analyze(
  series: MarketSeries,
  asOf: MarketTime
  ): List<TechnicalObservation>
  }

/**

* Major analytical capabilities available to the research laboratory.
  */
  enum class TechnicalCapability {
  
  TREND_ANALYSIS,
  
  MOMENTUM_ANALYSIS,
  
  MOVING_AVERAGES,
  
  VOLATILITY_ANALYSIS,
  
  OSCILLATORS,
  
  SWING_STRUCTURE,
  
  MARKET_STRUCTURE,
  
  SUPPORT_RESISTANCE,
  
  SUPPLY_DEMAND,
  
  LIQUIDITY_ANALYSIS,
  
  FAIR_VALUE_GAPS,
  
  SESSION_ANALYSIS,
  
  MARKET_REGIME,
  
  CUSTOM
  }
