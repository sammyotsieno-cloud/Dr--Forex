package com.drforex.researchlab.core.indicator

import com.drforex.researchlab.core.market.MarketCandle

/**

* Volume-derived quantitative measurements.

* 

* The meaning of volume depends on the underlying market-data source.

* For spot FX this may represent tick volume rather than centralized

* traded volume.

* 

* These calculations describe volume behaviour only and do not

* generate trading signals.
  */
  object VolumeMetrics {
  
  /**
  
  * Average volume over the requested period.
    */
    fun averageVolume(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Volume period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    return candles
    .takeLast(period)
    .map { it.volume }
    .filter { it.isFinite() }
    .averageOrNull()
    }
  
  /**
  
  * Latest volume relative to its recent average.
  
  * 
  
  * 1.0 means equal to the average.
  
  * Greater than 1.0 means above average.
  
  * Less than 1.0 means below average.
    */
    fun relativeVolume(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val average =
    averageVolume(
    candles = candles.dropLast(1),
    period = period
    )
    ?: return null
    
    val latest =
    candles.lastOrNull()?.volume
    ?: return null
    
    if (
    !latest.isFinite() ||
    average <= 0.0
    ) {
    return null
    }
    
    return latest / average
    }
  
  /**
  
  * Percentage difference between current volume and its
  
  * preceding rolling average.
    */
    fun volumeChangePercent(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    val average =
    averageVolume(
    candles = candles.dropLast(1),
    period = period
    )
    ?: return null
    
    val latest =
    candles.lastOrNull()?.volume
    ?: return null
    
    if (
    !latest.isFinite() ||
    average == 0.0
    ) {
    return null
    }
    
    return (
    (latest - average) /
    average
    ) * 100.0
    }
  
  /**
  
  * Signed volume pressure proxy.
  
  * 
  
  * Positive values correspond to candles closing above their open.
  
  * Negative values correspond to candles closing below their open.
  
  * Zero corresponds to a neutral candle.
  
  * 
  
  * This is a price-direction proxy, not a claim about actual
  
  * buy/sell order flow.
    */
    fun signedVolume(
    candle: MarketCandle
    ): Double {
    
    val volume =
    candle.volume
    
    return when {
    candle.close > candle.open ->
    volume
    
     candle.close < candle.open ->
     -volume

 else ->
     0.0
    
    }
    }
  
  /**
  
  * Sum of signed volume over a historical window.
    */
    fun cumulativeSignedVolume(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "Volume period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    return candles
    .takeLast(period)
    .sumOf {
    signedVolume(it)
    }
    }
  
  /**
  
  * Volume-weighted average price over the supplied window.
  
  * 
  
  * Typical price = (high + low + close) / 3.
  
  * 
  
  * Note: interpretation depends on whether the source volume
  
  * represents actual traded volume or tick volume.
    */
    fun vwap(
    candles: List<MarketCandle>,
    period: Int
    ): Double? {
    
    require(period > 0) {
    "VWAP period must be greater than zero."
    }
    
    if (candles.size < period) {
    return null
    }
    
    val window =
    candles.takeLast(period)
    
    var weightedPrice = 0.0
    var totalVolume = 0.0
    
    for (candle in window) {
    
     val volume =
     candle.volume

 if (
     !volume.isFinite() ||
     volume < 0.0
 ) {
     continue
 }

 val typicalPrice =
     (
         candle.high +
             candle.low +
             candle.close
         ) / 3.0

 weightedPrice +=
     typicalPrice * volume

 totalVolume += volume
    
    }
    
    if (totalVolume <= 0.0) {
    return null
    }
    
    return weightedPrice / totalVolume
    }
  
  /**
  
  * Measures the latest volume at a specific historical index.
  
  * 
  
  * Only candles before the supplied index are used as the
  
  * comparison baseline.
    */
    fun relativeVolumeAt(
    candles: List<MarketCandle>,
    period: Int,
    index: Int
    ): Double? {
    
    require(period > 0) {
    "Volume period must be greater than zero."
    }
    
    require(index >= 0) {
    "Index cannot be negative."
    }
    
    if (index >= candles.size) {
    return null
    }
    
    if (index < period) {
    return null
    }
    
    val latest =
    candles[index].volume
    
    val average =
    candles
    .subList(
    index - period,
    index
    )
    .map { it.volume }
    .filter { it.isFinite() }
    .averageOrNull()
    
    if (
    average == null ||
    average <= 0.0 ||
    !latest.isFinite()
    ) {
    return null
    }
    
    return latest / average
    }
  
  private fun List<Double>.averageOrNull(): Double? {
  
   if (isEmpty()) {
     return null
 }

 return average()
  
  }
  }
