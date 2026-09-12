package com.drforex.researchlab.core.strategy

import com.drforex.researchlab.core.time.MarketTime

/**
 * Complete point-in-time context supplied to strategy evaluation.
 *
 * The context combines the observation with the requirements that were
 * resolved for the strategy. It does not fetch data, calculate indicators,
 * or perform historical simulation.
 */
data class StrategyEvaluationContext(
    val observedAt: MarketTime,
    val observation: StrategyObservation,
    val requirements: StrategyObservationRequirements
) {

    init {
        require(observation.observedAt == observedAt) {
            "Strategy evaluation context time must match observation time."
        }
    }
}
