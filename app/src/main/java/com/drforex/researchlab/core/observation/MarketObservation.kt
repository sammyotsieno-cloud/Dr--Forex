package com.drforex.researchlab.core.observation

import com.drforex.researchlab.core.time.MarketTime

/**

* A canonical, point-in-time observation available to DrForex.

* 

* Observations are deliberately generic. They allow market prices,

* technical measurements, macroeconomic information, news-derived

* facts, cross-asset relationships, and other evidence to enter the

* research system through a common temporal structure.

* 

* An observation describes what was known or measurable at a given

* point in time. It does not itself represent a prediction or conclusion.
  */
  data class MarketObservation(
  val observedAt: MarketTime,
  val source: ObservationSource,
  val subject: String,
  val type: ObservationType,
  val value: Double? = null,
  val text: String? = null,
  val confidence: Double? = null
  ) {
  
  init {
  require(subject.isNotBlank()) {
  "Observation subject must not be blank."
  }
  
   confidence?.let {
     require(it in 0.0..1.0) {
         "Observation confidence must be between 0.0 and 1.0."
     }
 }

 require(value != null || !text.isNullOrBlank()) {
     "An observation must contain either a numeric value or text."
 }
  
  }
  }

enum class ObservationSource {
MARKET_DATA,
ECONOMIC_DATA,
NEWS,
CENTRAL_BANK,
GOVERNMENT,
TECHNICAL_ENGINE,
FUNDAMENTAL_ENGINE,
CROSS_ASSET_ANALYSIS,
RESEARCH_ENGINE,
USER_PROVIDED
}

enum class ObservationType {
PRICE,
VOLUME,
VOLATILITY,
TECHNICAL,
MARKET_STRUCTURE,
ECONOMIC,
POLICY,
NEWS_FACT,
SENTIMENT,
CORRELATION,
RELATIONSHIP,
EVENT,
OTHER
}
