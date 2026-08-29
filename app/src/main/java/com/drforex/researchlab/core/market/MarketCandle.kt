package com.drforex.researchlab.core.market

import com.drforex.researchlab.core.time.MarketTime

/**

* Canonical point-in-time OHLCV representation.

* 

* A candle is immutable research data. Its timestamp represents the

* opening time of the candle, while closeTime represents when the

* candle became fully observable.

* 

* DrForex must only use a candle for decisions after closeTime.
  */
  data class MarketCandle(
  val instrument: MarketInstrument,
  val timeframe: MarketTimeframe,
  val openTime: MarketTime,
  val closeTime: MarketTime,
  val open: Double,
  val high: Double,
  val low: Double,
  val close: Double,
  val volume: Double? = null
  ) {
  
  init {
  require(closeTime.isAfter(openTime)) {
  "Candle close time must be after open time."
  }
  
   require(high >= open) {
     "High must be greater than or equal to open."
 }

 require(high >= close) {
     "High must be greater than or equal to close."
 }

 require(low <= open) {
     "Low must be less than or equal to open."
 }

 require(low <= close) {
     "Low must be less than or equal to close."
 }

 require(high >= low) {
     "High must be greater than or equal to low."
 }

 require(open >= 0.0) {
     "Open price cannot be negative."
 }

 require(high >= 0.0) {
     "High price cannot be negative."
 }

 require(low >= 0.0) {
     "Low price cannot be negative."
 }

 require(close >= 0.0) {
     "Close price cannot be negative."
 }

 volume?.let {
     require(it >= 0.0) {
         "Volume cannot be negative."
     }
 }
  
  }
  
  /**
  
  * Returns true when the candle is fully closed and therefore
  * available for point-in-time research at the supplied time.
    */
    fun isClosedAt(time: MarketTime): Boolean =
    closeTime.isAtOrBefore(time)
  
  /**
  
  * True when the candle has not yet closed at the supplied time.
    */
    fun isStillFormingAt(time: MarketTime): Boolean =
    !isClosedAt(time)
  
  val range: Double
  get() = high - low
  
  val body: Double
  get() = kotlin.math.abs(close - open)
  
  val isBullish: Boolean
  get() = close > open
  
  val isBearish: Boolean
  get() = close < open
  
  val isDoji: Boolean
  get() = close == open
  }
