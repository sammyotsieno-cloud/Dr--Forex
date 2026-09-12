package com.drforex.researchlab.core.strategy

import com.drforex.researchlab.core.time.MarketTime

/**
 * Canonical definition of a research strategy.
 *
 * A strategy defines a systematic decision framework that can later be
 * evaluated through experiments, backtests, robustness analysis, and
 * out-of-sample validation.
 *
 * This model describes the strategy only.
 * It does not execute trades, perform backtests, manage capital or risk,
 * or compose multiple strategies.
 */
data class StrategyDefinition(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: MarketTime,
    val instrument: String? = null,
    val timeframe: String? = null,
    val entryRules: List<StrategyRule> = emptyList(),
    val exitRules: List<StrategyRule> = emptyList(),
    val filters: List<StrategyRule> = emptyList(),
    val assumptions: List<String> = emptyList(),
    val parameters: List<StrategyParameter> = emptyList(),
    val status: StrategyDefinitionStatus = StrategyDefinitionStatus.DRAFT
) {

    init {
        require(id.isNotBlank()) {
            "Strategy id must not be blank."
        }

        require(name.isNotBlank()) {
            "Strategy name must not be blank."
        }

        require(description.isNotBlank()) {
            "Strategy description must not be blank."
        }

        require(instrument == null || instrument.isNotBlank()) {
            "Strategy instrument must not be blank when provided."
        }

        require(timeframe == null || timeframe.isNotBlank()) {
            "Strategy timeframe must not be blank when provided."
        }

        val allRuleNames = entryRules.map { it.name } +
            exitRules.map { it.name } +
            filters.map { it.name }

        require(allRuleNames.all { it.isNotBlank() }) {
            "Strategy rule names must not be blank."
        }

        require(allRuleNames.size == allRuleNames.toSet().size) {
            "Strategy rule names must be unique."
        }

        require(parameters.map { it.name }.size ==
            parameters.map { it.name }.toSet().size) {
            "Strategy parameter names must be unique."
        }

        require(parameters.all { it.name.isNotBlank() }) {
            "Strategy parameter names must not be blank."
        }

        require(assumptions.all { it.isNotBlank() }) {
            "Strategy assumptions must not be blank."
        }
    }
}

/**
 * A rule that contributes to a strategy decision.
 *
 * Rules remain declarative at this layer. Evaluation logic belongs to later
 * strategy evaluation/execution components.
 */
data class StrategyRule(
    val name: String,
    val description: String,
    val type: StrategyRuleType
) {
    init {
        require(name.isNotBlank()) {
            "Strategy rule name must not be blank."
        }

        require(description.isNotBlank()) {
            "Strategy rule description must not be blank."
        }
    }
}

enum class StrategyRuleType {
    ENTRY,
    EXIT,
    FILTER,
    CONFIRMATION,
    INVALIDATION
}

/**
 * A configurable strategy parameter.
 *
 * The definition records the parameter and its intended domain without
 * performing optimization. Parameter optimization belongs to later research
 * and robustness workflows.
 */
data class StrategyParameter(
    val name: String,
    val description: String,
    val type: StrategyParameterType,
    val defaultValue: Double? = null,
    val minimumValue: Double? = null,
    val maximumValue: Double? = null
) {
    init {
        require(name.isNotBlank()) {
            "Strategy parameter name must not be blank."
        }

        require(description.isNotBlank()) {
            "Strategy parameter description must not be blank."
        }

        require(
            minimumValue == null || maximumValue == null ||
                minimumValue <= maximumValue
        ) {
            "Strategy parameter minimum must not exceed maximum."
        }

        require(
            defaultValue == null ||
                (minimumValue == null || defaultValue >= minimumValue) &&
                (maximumValue == null || defaultValue <= maximumValue)
        ) {
            "Strategy parameter default value must be within its defined range."
        }
    }
}

enum class StrategyParameterType {
    INTEGER,
    DECIMAL,
    PERCENTAGE,
    PRICE_DISTANCE,
    TIME_PERIOD
}

enum class StrategyDefinitionStatus {
    DRAFT,
    DEFINED,
    READY_FOR_EVALUATION,
    UNDER_EVALUATION,
    VALIDATED,
    INVALIDATED,
    RETIRED
}
