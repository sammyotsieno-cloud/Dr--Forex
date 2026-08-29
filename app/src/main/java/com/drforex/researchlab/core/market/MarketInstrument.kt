package com.drforex.researchlab.core.market

/**

* Canonical identity of a financial market instrument.

* 

* DrForex must never rely on display names alone. A stable instrument

* identity allows observations, events, relationships, research runs,

* and historical datasets to be linked consistently.
  */
  data class MarketInstrument(
  val symbol: String,
  val assetClass: AssetClass,
  val baseCurrency: String? = null,
  val quoteCurrency: String? = null
  ) {
  
  init {
  require(symbol.isNotBlank()) {
  "Instrument symbol must not be blank."
  }
  
   require(baseCurrency == null || baseCurrency.length == 3) {
     "Base currency must use a three-letter currency code."
 }

 require(quoteCurrency == null || quoteCurrency.length == 3) {
     "Quote currency must use a three-letter currency code."
 }
  
  }
  
  companion object {
  
   fun forex(
     baseCurrency: String,
     quoteCurrency: String
 ): MarketInstrument {
     val base = baseCurrency.uppercase()
     val quote = quoteCurrency.uppercase()

     return MarketInstrument(
         symbol = "$base$quote",
         assetClass = AssetClass.FOREX,
         baseCurrency = base,
         quoteCurrency = quote
     )
 }
  
  }
  }

enum class AssetClass {
FOREX,
EQUITY,
INDEX,
COMMODITY,
BOND,
CRYPTO,
ETF,
OTHER
}
