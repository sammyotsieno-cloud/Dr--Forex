package com.drforex.researchlab.core.market

import com.drforex.researchlab.core.time.MarketTime

/**

* Immutable chronological collection of market candles belonging to one

* instrument and one timeframe.

* 

* MarketSeries is intentionally a raw-data structure. Technical,

* structural, and statistical interpretations belong in higher-level

* research engines.
  */
  data class MarketSeries(
  val instrument: MarketInstrument,
  val timeframe: MarketTimeframe,
  val candles: List<MarketCandle>
  ) {
  
  init {
  require(candles.all { it.instrument == instrument }) {
  "All candles must belong to the series instrument."
  }
  
   require(candles.all { it.timeframe == timeframe }) {
     "All candles must belong to the series timeframe."
 }

 require(
     candles.zipWithNext().all { (current, next) ->
         current.openTime.isBefore(next.openTime)
     }
 ) {
     "Market candles must be strictly chronological."
 }
  
  }
  
  val size: Int
  get() = candles.size
  
  val isEmpty: Boolean
  get() = candles.isEmpty()
  
  val first: MarketCandle?
  get() = candles.firstOrNull()
  
  val last: MarketCandle?
  get() = candles.lastOrNull()
  
  /**
  
  * Returns candles whose opening time is within the requested range.
    */
    fun between(
    start: MarketTime,
    end: MarketTime
    ): List<MarketCandle> =
    candles.filter { candle ->
    candle.openTime.isAtOrAfter(start) &&
    candle.openTime.isAtOrBefore(end)
    }
  
  /**
  
  * Returns the most recent candles available at a specific point in time.
  * 
  * Only candles that have already closed are eligible.
    */
    fun availableAt(time: MarketTime): List<MarketCandle> =
    candles.filter { it.isClosedAt(time) }
  
  /**
  
  * Returns the latest closed candle available at the supplied time.
    */
    fun latestClosedAt(time: MarketTime): MarketCandle? =
    candles.lastOrNull { it.isClosedAt(time) }
  
  /**
  
  * Returns the previous closed candle relative to a reference candle.
    */
    fun previousClosed(
    reference: MarketCandle
    ): MarketCandle? {
    val index = candles.indexOf(reference)
    
    if (index <= 0) return null
    
    return candles
    .subList(0, index)
    .lastOrNull()
    }
  
  /**
  
  * Detects chronological gaps based on the expected timeframe duration.
  
  * 
  
  * This is intentionally a diagnostic rather than an automatic repair.
  
  * Missing market data must never silently be fabricated.
    */
    fun findGaps(): List<MarketGap> {
    if (candles.size < 2) return emptyList()
    
    return candles
    .zipWithNext()
    .mapNotNull { (previous, current) ->
    val expectedOpen =
    previous.openTime.plusSeconds(timeframe.durationSeconds)
    
         if (current.openTime.isAfter(expectedOpen)) {
         MarketGap(
             expectedAt = expectedOpen,
             actualNextAt = current.openTime
         )
     } else {
         null
     }
 }
  
  }
  }

/**

* Represents a detected gap in an otherwise chronological market series.
  */
  data class MarketGap(
  val expectedAt: MarketTime,
  val actualNextAt: MarketTime
  )
