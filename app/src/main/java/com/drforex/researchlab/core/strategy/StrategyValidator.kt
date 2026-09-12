package com.drforex.researchlab.core.strategy

/**
 * Validates a strategy definition before it is allowed to move into
 * later strategy evaluation workflows.
 *
 * This validator is intentionally limited to strategy-definition integrity.
 * It does not evaluate whether a strategy is profitable, statistically
 * significant, robust, or suitable for live trading.
 */
class StrategyValidator {

    fun validate(
        strategy: StrategyDefinition
    ): StrategyValidation {
        val errors = mutableListOf<String>()

        if (strategy.id.isBlank()) {
            errors += "Strategy id must not be blank."
        }

        if (strategy.name.isBlank()) {
            errors += "Strategy name must not be blank."
        }

        if (strategy.description.isBlank()) {
            errors += "Strategy description must not be blank."
        }

        if (strategy.instrument != null && strategy.instrument.isBlank()) {
            errors += "Strategy instrument must not be blank when provided."
        }

        if (strategy.timeframe != null && strategy.timeframe.isBlank()) {
            errors += "Strategy timeframe must not be blank when provided."
        }

        validateRules(
            rules = strategy.entryRules,
            expectedType = StrategyRuleType.ENTRY,
            groupName = "entry",
            errors = errors
        )

        validateRules(
            rules = strategy.exitRules,
            expectedType = StrategyRuleType.EXIT,
            groupName = "exit",
            errors = errors
        )

        validateRules(
            rules = strategy.filters,
            expectedType = StrategyRuleType.FILTER,
            groupName = "filter",
            errors = errors
        )

        validateRuleNames(strategy, errors)
        validateParameterNames(strategy, errors)
        validateParameters(strategy, errors)
        validateAssumptions(strategy, errors)

        return if (errors.isEmpty()) {
            StrategyValidation.Valid
        } else {
            StrategyValidation.Invalid(errors)
        }
    }

    private fun validateRules(
        rules: List<StrategyRule>,
        expectedType: StrategyRuleType,
        groupName: String,
        errors: MutableList<String>
    ) {
        rules.forEach { rule ->
            if (rule.name.isBlank()) {
                errors += "$groupName strategy rule name must not be blank."
            }

            if (rule.description.isBlank()) {
                errors += "$groupName strategy rule description must not be blank."
            }

            if (rule.type != expectedType) {
                errors +=
                    "$groupName strategy rule '${rule.name}' must have type $expectedType."
            }
        }
    }

    private fun validateRuleNames(
        strategy: StrategyDefinition,
        errors: MutableList<String>
    ) {
        val ruleNames =
            strategy.entryRules.map { it.name } +
                strategy.exitRules.map { it.name } +
                strategy.filters.map { it.name }

        if (ruleNames.size != ruleNames.toSet().size) {
            errors += "Strategy rule names must be unique."
        }
    }

    private fun validateParameterNames(
        strategy: StrategyDefinition,
        errors: MutableList<String>
    ) {
        val parameterNames = strategy.parameters.map { it.name }

        if (parameterNames.size != parameterNames.toSet().size) {
            errors += "Strategy parameter names must be unique."
        }
    }

    private fun validateParameters(
        strategy: StrategyDefinition,
        errors: MutableList<String>
    ) {
        strategy.parameters.forEach { parameter ->
            if (parameter.name.isBlank()) {
                errors += "Strategy parameter name must not be blank."
            }

            if (parameter.description.isBlank()) {
                errors +=
                    "Strategy parameter '${parameter.name}' description must not be blank."
            }

            val minimum = parameter.minimumValue
            val maximum = parameter.maximumValue
            val default = parameter.defaultValue

            if (minimum != null && maximum != null && minimum > maximum) {
                errors +=
                    "Strategy parameter '${parameter.name}' minimum must not exceed maximum."
            }

            if (default != null && minimum != null && default < minimum) {
                errors +=
                    "Strategy parameter '${parameter.name}' default value must not be below minimum."
            }

            if (default != null && maximum != null && default > maximum) {
                errors +=
                    "Strategy parameter '${parameter.name}' default value must not exceed maximum."
            }
        }
    }

    private fun validateAssumptions(
        strategy: StrategyDefinition,
        errors: MutableList<String>
    ) {
        strategy.assumptions.forEachIndexed { index, assumption ->
            if (assumption.isBlank()) {
                errors +=
                    "Strategy assumption at index $index must not be blank."
            }
        }
    }
}

/**
 * Result of validating a strategy definition.
 */
sealed interface StrategyValidation {

    data object Valid : StrategyValidation

    data class Invalid(
        val errors: List<String>
    ) : StrategyValidation
}
