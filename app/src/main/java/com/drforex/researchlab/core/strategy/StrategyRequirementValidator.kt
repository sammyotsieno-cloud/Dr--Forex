package com.drforex.researchlab.core.strategy

/**
 * Validates the observation requirements declared by a strategy.
 *
 * This validator checks only structural integrity. It does not determine
 * whether a requirement is scientifically useful or whether a market data
 * source can provide it.
 */
class StrategyRequirementValidator {

    fun validate(
        requirements: StrategyObservationRequirements
    ): StrategyRequirementValidation {
        val errors = mutableListOf<String>()

        val names = requirements.requiredVariables.map { it.name }

        if (names.size != names.toSet().size) {
            errors += "Strategy observation requirement names must be unique."
        }

        requirements.requiredVariables.forEach { requirement ->
            if (requirement.name.isBlank()) {
                errors +=
                    "Strategy observation requirement name must not be blank."
            }

            if (requirement.description.isBlank()) {
                errors +=
                    "Strategy observation requirement '${requirement.name}' " +
                        "description must not be blank."
            }
        }

        return if (errors.isEmpty()) {
            StrategyRequirementValidation.Valid
        } else {
            StrategyRequirementValidation.Invalid(errors)
        }
    }
}

/**
 * Result of validating strategy observation requirements.
 */
sealed interface StrategyRequirementValidation {

    data object Valid : StrategyRequirementValidation

    data class Invalid(
        val errors: List<String>
    ) : StrategyRequirementValidation
}
