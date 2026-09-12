package com.drforex.researchlab.core.strategy

/**
 * Coordinates strategy validation and point-in-time strategy evaluation.
 *
 * The engine does not:
 * - fetch market data
 * - run historical simulations
 * - calculate performance
 * - optimize parameters
 * - manage capital or risk
 * - execute orders
 *
 * Those responsibilities belong to later workflow clusters.
 */
class StrategyEngine(
    private val strategyValidator: StrategyValidator = StrategyValidator(),
    private val strategyEvaluator: StrategyEvaluator
) {

    /**
     * Validates a strategy definition before evaluation.
     */
    fun validate(
        strategy: StrategyDefinition
    ): StrategyValidation {
        return strategyValidator.validate(strategy)
    }

    /**
     * Evaluates a strategy against one point-in-time observation.
     *
     * Evaluation is allowed only when the strategy definition is valid.
     */
    fun evaluate(
        strategy: StrategyDefinition,
        observation: StrategyObservation
    ): StrategyEvaluationResult {
        val validation = strategyValidator.validate(strategy)

        if (validation is StrategyValidation.Invalid) {
            return StrategyEvaluationResult.InvalidDefinition(
                errors = validation.errors
            )
        }

        return try {
            val evaluation = strategyEvaluator.evaluate(
                strategy = strategy,
                observation = observation
            )

            if (evaluation.strategyId != strategy.id) {
                StrategyEvaluationResult.InvalidEvaluation(
                    error = "Strategy evaluation id does not match strategy definition id."
                )
            } else if (evaluation.observedAt != observation.observedAt) {
                StrategyEvaluationResult.InvalidEvaluation(
                    error = "Strategy evaluation time does not match observation time."
                )
            } else {
                StrategyEvaluationResult.Success(evaluation)
            }
        } catch (exception: IllegalArgumentException) {
            StrategyEvaluationResult.InvalidEvaluation(
                error = exception.message
                    ?: "Strategy evaluation rejected the supplied observation."
            )
        }
    }
}

/**
 * Result of a strategy-engine evaluation request.
 */
sealed interface StrategyEvaluationResult {

    data class Success(
        val evaluation: StrategyEvaluation
    ) : StrategyEvaluationResult

    data class InvalidDefinition(
        val errors: List<String>
    ) : StrategyEvaluationResult

    data class InvalidEvaluation(
        val error: String
    ) : StrategyEvaluationResult
}
