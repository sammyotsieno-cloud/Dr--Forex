package com.drforex.researchlab.core.strategy

/**
 * Defines the contract for evaluating a strategy definition against
 * an already-prepared market observation.
 *
 * This is a decision-evaluation contract, not a backtesting engine.
 *
 * It must not:
 * - fetch market data
 * - simulate a sequence of trades
 * - calculate profitability
 * - manage capital or risk
 * - optimize strategy parameters
 *
 * Those responsibilities belong to later workflow components.
 */
interface StrategyEvaluator {

    fun evaluate(
        strategy: StrategyDefinition,
        observation: StrategyObservation
    ): StrategyEvaluation
}

/**
 * A point-in-time set of values available to the strategy evaluator.
 *
 * The observation represents information that is already available at
 * the observation time. It does not contain future information.
 */
data class StrategyObservation(
    val observedAt: com.drforex.researchlab.core.time.MarketTime,
    val values: Map<String, Double>
) {
    init {
        require(values.isNotEmpty()) {
            "Strategy observation must contain at least one value."
        }

        require(values.keys.all { it.isNotBlank() }) {
            "Strategy observation variable names must not be blank."
        }

        require(values.values.all { it.isFinite() }) {
            "Strategy observation values must be finite."
        }
    }
}

/**
 * Result of evaluating a strategy at one observation point.
 *
 * This describes the strategy's decision state only. It does not represent
 * a trade, order, fill, profit/loss result, or portfolio state.
 */
data class StrategyEvaluation(
    val strategyId: String,
    val observedAt: com.drforex.researchlab.core.time.MarketTime,
    val decision: StrategyDecision,
    val triggeredRules: List<String> = emptyList(),
    val notes: List<String> = emptyList()
) {
    init {
        require(strategyId.isNotBlank()) {
            "Strategy id must not be blank."
        }

        require(triggeredRules.all { it.isNotBlank() }) {
            "Triggered strategy rule names must not be blank."
        }

        require(notes.all { it.isNotBlank() }) {
            "Strategy evaluation notes must not be blank."
        }
    }
}

/**
 * Decision state produced by a strategy evaluation.
 *
 * These are research-level strategy decisions, not executable orders.
 */
enum class StrategyDecision {
    NO_ACTION,
    LONG_BIAS,
    SHORT_BIAS,
    LONG_SIGNAL,
    SHORT_SIGNAL,
    EXIT_LONG,
    EXIT_SHORT,
    INVALID
}
