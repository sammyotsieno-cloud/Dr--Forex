package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Common contract for point-in-time technical indicators.

* 

* Implementations must calculate values using only market information

* that was available at [asOf].

* 

* Indicators are descriptive research features. They do not generate

* trading signals by themselves.
  */
  interface TechnicalIndicator {
  
  /**
  
  * Stable identifier for the indicator.
  * 
  * Examples:
  * EMA_20
  * ATR_14
  * RSI_14
    */
    val id: String
  
  /**
  
  * Human-readable indicator name.
    */
    val name: String
  
  /**
  
  * Calculates the indicator value using information available
  * at [asOf].
  * 
  * Returns null when insufficient historical data exists.
    */
    fun calculate(
    series: MarketSeries,
    asOf: MarketTime
    ): Double?
    }

/**

* Describes the configuration used to construct a technical

* indicator.

* 

* Keeping configuration explicit allows experiments to record the

* exact indicator definition used during research.
  */
  data class IndicatorConfiguration(
  val indicatorId: String,
  val parameters: Map<String, Double>
  ) {
  
  init {
  require(indicatorId.isNotBlank()) {
  "Indicator ID cannot be blank."
  }
  
   require(
     parameters.keys.all { it.isNotBlank() }
 ) {
     "Indicator parameter names cannot be blank."
 }

 require(
     parameters.values.all { it.isFinite() }
 ) {
     "Indicator parameters must be finite."
 }
  
  }
  }
