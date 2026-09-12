package com.drforex.researchlab.core.strategy

/**
 * Validates a point-in-time observation before it reaches strategy
 * evaluation.
 *
 * This validator is responsible only for observation integrity.
 * It does not determine whether the observation contains the "right"
 * market variables for a particular strategy. That requires strategy
 * semantics and belongs to a later evaluation layer.
 */
class StrategyObservationValidator {

    fun validate(
        observation: StrategyObservation
    ): StrategyObservationValidation {
        val errors = mutableListOf<String>()

        if (observation.values.isEmpty()) {
            errors += "Strategy observation must contain at least one value."
        }

        if (observation.values.keys.any { it.isBlank() }) {
            errors += "Strategy observation variable names must not be blank."
        }

        if (observation.values.values.any { !it.isFinite() }) {
            errors += "Strategy observation values must be finite."
        }

        return if (errors.isEmpty()) {
            StrategyObservationValidation.Valid
        } else {
            StrategyObservationValidation.Invalid(errors)
        }
    }
}

/**
 * Result of validating a strategy observation.
 */
sealed interface StrategyObservationValidation {

    data object Valid : StrategyObservationValidation

    data class Invalid(
        val errors: List<String>
    ) : StrategyObservationValidation
}
