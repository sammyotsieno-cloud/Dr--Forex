package com.drforex.researchlab.core.analysis

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Builds an immutable point-in-time MarketState from technical

* observations produced by the analytical layer.

* 

* This class performs composition only. It does not calculate

* indicators or make trading decisions.
  */
  class MarketStateBuilder {
  
  fun build(
  series: MarketSeries,
  asOf: MarketTime,
  observations: List<TechnicalObservation>
  ): MarketState {
  
   val pointInTimeObservations = observations.filter {
     it.instrument == series.instrument &&
         it.timeframe == series.timeframe &&
         it.observedAt.isAtOrBefore(asOf)
 }

 val quality = when {
     pointInTimeObservations.isEmpty() ->
         MarketStateQuality.UNAVAILABLE

     else ->
         MarketStateQuality.COMPLETE
 }

 return MarketState(
     instrument = series.instrument,
     timeframe = series.timeframe,
     asOf = asOf,
     observations = pointInTimeObservations,
     quality = quality
 )
  
  }
  }
