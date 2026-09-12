package com.drforex.researchlab.core.strategy

/**
 * Describes the observation variables required by a strategy.
 *
 * This model defines what information a strategy needs at evaluation time.
 * It does not fetch, calculate, or validate market data.
 */
data class StrategyObservationRequirements(
    val requiredVariables: List<StrategyObservationRequirement> = emptyList()
) {

    init {
        require(
            requiredVariables.map { it.name }.size ==
                requiredVariables.map { it.name }.toSet().size
        ) {
            "Strategy observation requirement names must be unique."
        }
    }
}

/**
 * A single variable required by a strategy evaluation.
 */
data class StrategyObservationRequirement(
    val name: String,
    val description: String,
    val required: Boolean = true
) {

    init {
        require(name.isNotBlank()) {
            "Strategy observation requirement name must not be blank."
        }

        require(description.isNotBlank()) {
            "Strategy observation requirement description must not be blank."
        }
    }
}
