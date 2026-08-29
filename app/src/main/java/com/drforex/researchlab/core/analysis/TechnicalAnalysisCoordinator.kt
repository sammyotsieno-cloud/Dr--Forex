package com.drforex.researchlab.core.analysis

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Coordinates multiple technical-analysis capabilities.

* 

* The coordinator is responsible for composition and execution order,

* not for implementing individual analytical calculations.
  */
  class TechnicalAnalysisCoordinator(
  private val analyzers: List<TechnicalAnalyzer>
  ) : TechnicalAnalysisEngine {
  
  override fun analyze(
  series: MarketSeries,
  asOf: MarketTime
  ): List<TechnicalObservation> {
  
   val eligibleCandles = series.availableAt(asOf)

 if (eligibleCandles.isEmpty()) {
     return emptyList()
 }

 return analyzers
     .flatMap { analyzer ->
         analyzer.analyze(series, asOf)
     }
     .filter { observation ->
         observation.instrument == series.instrument &&
             observation.timeframe == series.timeframe &&
             observation.observedAt.isAtOrBefore(asOf)
     }
  
  }
  
  /**
  
  * Returns the analytical capabilities currently registered.
    */
    fun capabilities(): Set<TechnicalCapability> =
    analyzers.map { it.capability }.toSet()
    }
