package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketCandle
import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Detects confirmed swing highs and swing lows from a chronological

* market series.

* 

* A swing is only returned when the candles required for its

* confirmation are already available at [asOf].

* 

* This detector does not predict future swings.
  */
  class SwingPointDetector {
  
  /**
  
  * Detects confirmed swing points using a symmetric left/right
  
  * window.
  
  * 
  
  * [leftBars] describes how many candles must exist before the
  
  * candidate.
  
  * 
  
  * [rightBars] describes how many candles are required after the
  
  * candidate before it can be confirmed.
    */
    fun detect(
    series: MarketSeries,
    asOf: MarketTime,
    leftBars: Int = 2,
    rightBars: Int = 2
    ): List<SwingPoint> {
    
    require(leftBars >= 1) {
    "leftBars must be at least 1."
    }
    
    require(rightBars >= 1) {
    "rightBars must be at least 1."
    }
    
    val available = series.availableAt(asOf)
    
    if (available.size < leftBars + rightBars + 1) {
    return emptyList()
    }
    
    val swings = mutableListOf<SwingPoint>()
    
    for (index in leftBars until available.size - rightBars) {
    
     val candidate = available[index]

 val leftWindow =
     available.subList(index - leftBars, index)

 val rightWindow =
     available.subList(index + 1, index + rightBars + 1)

 val surrounding =
     leftWindow + rightWindow

 val isSwingHigh =
     surrounding.all { candidate.high > it.high }

 val isSwingLow =
     surrounding.all { candidate.low < it.low }

 if (isSwingHigh) {
     val confirmationCandle =
         rightWindow.last()

     swings += SwingPoint(
         time = candidate.openTime,
         price = candidate.high,
         type = SwingPointType.HIGH,
         confirmationTime = confirmationCandle.closeTime,
         strength = leftBars + rightBars
     )
 }

 if (isSwingLow) {
     val confirmationCandle =
         rightWindow.last()

     swings += SwingPoint(
         time = candidate.openTime,
         price = candidate.low,
         type = SwingPointType.LOW,
         confirmationTime = confirmationCandle.closeTime,
         strength = leftBars + rightBars
     )
 }
    
    }
    
    return swings
    .distinctBy {
    Triple(
    it.time,
    it.type,
    it.price
    )
    }
    .sortedBy {
    it.time.instant
    }
    }
  
  /**
  
  * Determines whether a specific candle is a confirmed swing high.
    */
    fun isConfirmedSwingHigh(
    candles: List<MarketCandle>,
    candidateIndex: Int,
    leftBars: Int,
    rightBars: Int
    ): Boolean {
    
    if (
    candidateIndex < leftBars ||
    candidateIndex + rightBars >= candles.size
    ) {
    return false
    }
    
    val candidate = candles[candidateIndex]
    
    val leftWindow =
    candles.subList(
    candidateIndex - leftBars,
    candidateIndex
    )
    
    val rightWindow =
    candles.subList(
    candidateIndex + 1,
    candidateIndex + rightBars + 1
    )
    
    return (leftWindow + rightWindow)
    .all { candidate.high > it.high }
    }
  
  /**
  
  * Determines whether a specific candle is a confirmed swing low.
    */
    fun isConfirmedSwingLow(
    candles: List<MarketCandle>,
    candidateIndex: Int,
    leftBars: Int,
    rightBars: Int
    ): Boolean {
    
    if (
    candidateIndex < leftBars ||
    candidateIndex + rightBars >= candles.size
    ) {
    return false
    }
    
    val candidate = candles[candidateIndex]
    
    val leftWindow =
    candles.subList(
    candidateIndex - leftBars,
    candidateIndex
    )
    
    val rightWindow =
    candles.subList(
    candidateIndex + 1,
    candidateIndex + rightBars + 1
    )
    
    return (leftWindow + rightWindow)
    .all { candidate.low < it.low }
    }
    }
