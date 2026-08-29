package com.drforex.researchlab.core.regime

import com.drforex.researchlab.core.analysis.MarketState
import com.drforex.researchlab.core.analysis.TechnicalObservationType
import com.drforex.researchlab.core.time.MarketTime

/**

* Determines the observable market regime from an already constructed

* point-in-time MarketState.

* 

* This engine is deliberately conservative:

* it classifies the environment but never produces a trade signal.
  */
  class MarketRegimeEngine {
  
  fun classify(
  state: MarketState,
  asOf: MarketTime = state.asOf
  ): MarketRegime {
  
   require(!asOf.isBefore(state.asOf)) {
     "Regime classification cannot request a time before the MarketState."
 }

 val observations = state.observations

 if (observations.isEmpty()) {
     return MarketRegime(
         observedAt = asOf,
         classification = RegimeClassification.UNCERTAIN,
         confidence = 0.0
     )
 }

 val evidence = mutableListOf<RegimeEvidence>()

 val higherHighs =
     observations.count {
         it.type == TechnicalObservationType.HIGHER_HIGH
     }

 val higherLows =
     observations.count {
         it.type == TechnicalObservationType.HIGHER_LOW
     }

 val lowerHighs =
     observations.count {
         it.type == TechnicalObservationType.LOWER_HIGH
     }

 val lowerLows =
     observations.count {
         it.type == TechnicalObservationType.LOWER_LOW
     }

 val volatilityObservations =
     observations.filter {
         it.type == TechnicalObservationType.VOLATILITY
     }

 val bullishStructure =
     higherHighs > 0 && higherLows > 0

 val bearishStructure =
     lowerHighs > 0 && lowerLows > 0

 val hasTransitionEvidence =
     observations.any {
         it.type == TechnicalObservationType.CHANGE_OF_CHARACTER
     }

 if (bullishStructure) {
     evidence += RegimeEvidence(
         source = "market_structure",
         description = "Higher-high and higher-low structure detected.",
         weight = 1.0
     )
 }

 if (bearishStructure) {
     evidence += RegimeEvidence(
         source = "market_structure",
         description = "Lower-high and lower-low structure detected.",
         weight = 1.0
     )
 }

 if (hasTransitionEvidence) {
     evidence += RegimeEvidence(
         source = "market_structure",
         description = "Structural transition evidence detected.",
         weight = 1.0
     )
 }

 val classification =
     when {
         hasTransitionEvidence ->
             RegimeClassification.TRANSITION

         bullishStructure && !bearishStructure ->
             RegimeClassification.TRENDING_UP

         bearishStructure && !bullishStructure ->
             RegimeClassification.TRENDING_DOWN

         volatilityObservations.isNotEmpty() ->
             RegimeClassification.UNCERTAIN

         else ->
             RegimeClassification.RANGING
     }

 val confidence =
     when (classification) {
         RegimeClassification.TRENDING_UP,
         RegimeClassification.TRENDING_DOWN ->
             0.70

         RegimeClassification.TRANSITION ->
             0.60

         RegimeClassification.RANGING ->
             0.40

         else ->
             0.20
     }

 return MarketRegime(
     observedAt = asOf,
     classification = classification,
     confidence = confidence,
     evidence = evidence
 )
  
  }
  }
