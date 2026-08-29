package com.drforex.researchlab.core.validation

import com.drforex.researchlab.core.market.MarketCandle
import com.drforex.researchlab.core.market.MarketDataQuality
import com.drforex.researchlab.core.market.MarketDataQualityFinding
import com.drforex.researchlab.core.market.MarketDataValidationResult
import com.drforex.researchlab.core.market.MarketSeries

/**

* Deterministic validator for historical market data.

* 

* This validator does not repair or modify market data.

* It only identifies conditions that could make the data unsafe

* for quantitative research.
  */
  class MarketDataValidator {
  
  fun validate(candle: MarketCandle): MarketDataValidationResult {
  val findings = mutableListOf<MarketDataQualityFinding>()
  
   if (!candle.closeTime.isAfter(candle.openTime)) {
     findings += finding(
         code = "INVALID_TIME_RANGE",
         message = "Candle close time must occur after open time.",
         quality = MarketDataQuality.INVALID
     )
 }

 if (candle.high < candle.low) {
     findings += finding(
         code = "INVALID_HIGH_LOW",
         message = "High price is below low price.",
         quality = MarketDataQuality.INVALID
     )
 }

 if (candle.high < candle.open || candle.high < candle.close) {
     findings += finding(
         code = "PRICE_OUTSIDE_HIGH",
         message = "Open or close exceeds the reported high.",
         quality = MarketDataQuality.INVALID
     )
 }

 if (candle.low > candle.open || candle.low > candle.close) {
     findings += finding(
         code = "PRICE_OUTSIDE_LOW",
         message = "Open or close is below the reported low.",
         quality = MarketDataQuality.INVALID
     )
 }

 if (candle.open < 0.0 ||
     candle.high < 0.0 ||
     candle.low < 0.0 ||
     candle.close < 0.0
 ) {
     findings += finding(
         code = "NEGATIVE_PRICE",
         message = "Market prices cannot be negative.",
         quality = MarketDataQuality.INVALID
     )
 }

 candle.volume?.let {
     if (it < 0.0) {
         findings += finding(
             code = "NEGATIVE_VOLUME",
             message = "Volume cannot be negative.",
             quality = MarketDataQuality.INVALID
         )
     }
 }

 return result(findings)
  
  }
  
  fun validate(series: MarketSeries): MarketDataValidationResult {
  val findings = mutableListOf<MarketDataQualityFinding>()
  
   series.candles.forEachIndexed { index, candle ->
     val candleResult = validate(candle)

     findings += candleResult.findings.map { finding ->
         finding.copy(
             code = "CANDLE_${index}_${finding.code}"
         )
     }
 }

 series.candles.zipWithNext().forEachIndexed { index, (previous, current) ->

     if (!previous.openTime.isBefore(current.openTime)) {
         findings += finding(
             code = "NON_CHRONOLOGICAL_$index",
             message = "Candles are not strictly chronological.",
             quality = MarketDataQuality.INVALID
         )
     }

     val expectedNext =
         previous.openTime.plusSeconds(series.timeframe.durationSeconds)

     if (current.openTime.isAfter(expectedNext)) {
         findings += finding(
             code = "DATA_GAP_$index",
             message = "A chronological gap exists between consecutive candles.",
             quality = MarketDataQuality.SUSPECT
         )
     }
 }

 return result(findings)
  
  }
  
  private fun finding(
  code: String,
  message: String,
  quality: MarketDataQuality
  ): MarketDataQualityFinding =
  MarketDataQualityFinding(
  quality = quality,
  code = code,
  message = message
  )
  
  private fun result(
  findings: List<MarketDataQualityFinding>
  ): MarketDataValidationResult {
  
   val overallQuality = when {
     findings.any { it.quality == MarketDataQuality.INVALID } ->
         MarketDataQuality.INVALID

     findings.any { it.quality == MarketDataQuality.SUSPECT } ->
         MarketDataQuality.SUSPECT

     else ->
         MarketDataQuality.VALIDATED
 }

 return MarketDataValidationResult(
     quality = overallQuality,
     findings = findings
 )
  
  }
  }
