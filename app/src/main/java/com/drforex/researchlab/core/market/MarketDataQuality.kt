package com.drforex.researchlab.core.market

/**

* Quality state assigned to market data before it is consumed by
* research engines.
* 
* Research code must be able to distinguish valid observations from
* observations that require investigation.
  */
  enum class MarketDataQuality {
  UNKNOWN,
  VALIDATED,
  SUSPECT,
  INVALID
  }

/**

* Describes a validation finding attached to market data.
  */
  data class MarketDataQualityFinding(
  val quality: MarketDataQuality,
  val code: String,
  val message: String
  ) {
  
  init {
  require(code.isNotBlank()) {
  "Quality finding code must not be blank."
  }
  
   require(message.isNotBlank()) {
     "Quality finding message must not be blank."
 }
  
  }
  }

/**

* Result of validating a market-data object.
  */
  data class MarketDataValidationResult(
  val quality: MarketDataQuality,
  val findings: List<MarketDataQualityFinding> = emptyList()
  ) {
  
  val isUsableForResearch: Boolean
  get() = quality == MarketDataQuality.VALIDATED
  
  val requiresInvestigation: Boolean
  get() = quality == MarketDataQuality.SUSPECT ||
  quality == MarketDataQuality.INVALID
  }
