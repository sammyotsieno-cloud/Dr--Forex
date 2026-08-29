package com.drforex.researchlab.core.analysis

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Immutable request describing exactly what the analytical layer is

* permitted to know.

* 

* The request establishes the point-in-time boundary for analysis.
  */
  data class AnalysisRequest(
  val series: MarketSeries,
  val asOf: MarketTime,
  val minimumObservations: Int = 1
  ) {
  
  init {
  require(minimumObservations >= 1) {
  "Minimum observations must be at least 1."
  }
  }
  
  /**
  
  * Only information available at the requested point in time.
    */
    val availableCandles =
    series.availableAt(asOf)
  
  /**
  
  * Whether enough historical observations exist for the requested
  * analysis.
    */
    val hasSufficientHistory =
    availableCandles.size >= minimumObservations
    }

/**

* Describes why an analytical request cannot safely produce a result.
  */
  sealed interface AnalysisAvailability {
  
  data object Available : AnalysisAvailability
  
  data class InsufficientHistory(
  val required: Int,
  val available: Int
  ) : AnalysisAvailability
  
  data object NoDataAvailable : AnalysisAvailability
  }

/**

* Determines whether an analysis request has enough point-in-time data

* to proceed.
  */
  fun AnalysisRequest.availability(): AnalysisAvailability =
  when {
  availableCandles.isEmpty() ->
  AnalysisAvailability.NoDataAvailable
  
   availableCandles.size < minimumObservations ->
     AnalysisAvailability.InsufficientHistory(
         required = minimumObservations,
         available = availableCandles.size
     )

 else ->
     AnalysisAvailability.Available
  
  }
