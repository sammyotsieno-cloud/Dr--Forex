package com.drforex.researchlab.core.analysis

import com.drforex.researchlab.core.market.MarketInstrument
import com.drforex.researchlab.core.market.MarketTimeframe
import com.drforex.researchlab.core.time.MarketTime

/**

* A point-in-time technical observation derived from market data.

* 

* Technical observations are interpretations of raw market data.

* They must retain their timestamp, instrument, timeframe, and

* provenance so that later research can determine exactly what was

* known at the time.
  */
  data class TechnicalObservation(
  val instrument: MarketInstrument,
  val timeframe: MarketTimeframe,
  val observedAt: MarketTime,
  val type: TechnicalObservationType,
  val value: Double? = null,
  val confidence: Double? = null,
  val metadata: Map<String, String> = emptyMap()
  ) {
  
  init {
  confidence?.let {
  require(it in 0.0..1.0) {
  "Confidence must be between 0.0 and 1.0."
  }
  }
  }
  }

/**

* Categories of technical observations supported by the research

* architecture.

* 

* These are deliberately broad. Individual engines will later provide

* the actual calculations and interpretation.
  */
  enum class TechnicalObservationType {
  
  // Trend and direction
  TREND,
  TREND_STRENGTH,
  MOMENTUM,
  
  // Moving averages and volatility
  MOVING_AVERAGE,
  ATR,
  VOLATILITY,
  
  // Oscillators
  RSI,
  MACD,
  
  // Market structure
  SWING_HIGH,
  SWING_LOW,
  HIGHER_HIGH,
  HIGHER_LOW,
  LOWER_HIGH,
  LOWER_LOW,
  
  // Structural events
  BREAK_OF_STRUCTURE,
  CHANGE_OF_CHARACTER,
  
  // Price zones
  SUPPORT,
  RESISTANCE,
  SUPPLY_ZONE,
  DEMAND_ZONE,
  
  // Liquidity and imbalance
  LIQUIDITY_HIGH,
  LIQUIDITY_LOW,
  FAIR_VALUE_GAP,
  
  // Market context
  SESSION,
  RANGE,
  REGIME,
  
  // Generic calculated observation
  CUSTOM
  }
