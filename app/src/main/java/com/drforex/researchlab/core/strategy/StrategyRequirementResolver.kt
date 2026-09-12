package com.drforex.researchlab.core.strategy

/**
 * Resolves which observation variables a strategy requires.
 *
 * This component connects the strategy definition with its declared
 * observation requirements. It does not fetch market data or calculate
 * missing variables.
 */
class StrategyRequirementResolver {

    fun resolve(
        strategy: StrategyDefinition,
        requirements: StrategyObservationRequirements
    ): StrategyRequirementResolution {
        val strategyVariables = buildSet {
            strategy.parameters.forEach { add(it.name) }
            strategy.entryRules.forEach { add(it.name) }
            strategy.exitRules.forEach { add(it.name) }
            strategy.filters.forEach { add(it.name) }
        }

        val unresolvedRequirements = requirements.requiredVariables
            .filter { requirement ->
                requirement.required &&
                    requirement.name !in strategyVariables
            }
            .map { it.name }

        return if (unresolvedRequirements.isEmpty()) {
            StrategyRequirementResolution.Resolved(
                requirements = requirements
            )
        } else {
            StrategyRequirementResolution.Unresolved(
                missingRequirements = unresolvedRequirements
            )
        }
    }
}

/**
 * Result of resolving strategy observation requirements.
 */
sealed interface StrategyRequirementResolution {

    data class Resolved(
        val requirements: StrategyObservationRequirements
    ) : StrategyRequirementResolution

    data class Unresolved(
        val missingRequirements: List<String>
    ) : StrategyRequirementResolution
}
