package com.drforex.researchlab.core.research

import com.drforex.researchlab.core.time.MarketTime

/**
 * A formally defined proposition derived from a research question.
 *
 * A ResearchHypothesis expresses an expected relationship, difference,
 * condition, or predictive relationship that can later be tested by the
 * research experiment infrastructure.
 *
 * A hypothesis is an expectation to be tested, not an established fact.
 *
 * This model does not:
 * - test market data
 * - calculate statistical significance
 * - determine profitability
 * - generate a trading signal
 * - approve a strategy
 * - produce a forecast
 */
data class ResearchHypothesis(
    val id: String,
    val questionId: String,
    val statement: String,
    val prediction: String,
    val relationship: HypothesisRelationship,
    val independentVariables: List<String>,
    val dependentVariable: String,
    val testConditions: List<ResearchCondition>,
    val outcomeDefinition: String,
    val assumptions: List<String> = emptyList(),
    val createdAt: MarketTime,
    val source: ResearchHypothesisSource = ResearchHypothesisSource.AUTOMATIC,
    val status: ResearchHypothesisStatus = ResearchHypothesisStatus.PROPOSED
) {
    init {
        require(id.isNotBlank()) {
            "Research hypothesis id must not be blank."
        }

        require(questionId.isNotBlank()) {
            "Research hypothesis question id must not be blank."
        }

        require(statement.isNotBlank()) {
            "Research hypothesis statement must not be blank."
        }

        require(prediction.isNotBlank()) {
            "Research hypothesis prediction must not be blank."
        }

        require(independentVariables.isNotEmpty()) {
            "A research hypothesis must define at least one independent variable."
        }

        require(independentVariables.all { it.isNotBlank() }) {
            "Independent variable names must not be blank."
        }

        require(dependentVariable.isNotBlank()) {
            "Dependent variable must not be blank."
        }

        require(outcomeDefinition.isNotBlank()) {
            "Research hypothesis must define an outcome."
        }

        require(independentVariables.distinct().size == independentVariables.size) {
            "Independent variables must have unique names."
        }

        require(assumptions.all { it.isNotBlank() }) {
            "Hypothesis assumptions must not contain blank values."
        }
    }
}

/**
 * Describes the type of relationship the hypothesis proposes to investigate.
 *
 * These values describe the research proposition only.
 * They do not imply that the proposed relationship actually exists.
 */
enum class HypothesisRelationship {
    POSITIVE,
    NEGATIVE,
    DIFFERENCE,
    ASSOCIATION,
    CONDITIONAL,
    NON_LINEAR,
    COMPARATIVE,
    PREDICTIVE
}

/**
 * Describes how the hypothesis was created.
 *
 * AUTOMATIC is the normal path for autonomous Dr. Forex research.
 *
 * USER_DIRECTED represents a hypothesis derived from a user-supplied
 * research objective or question.
 */
enum class ResearchHypothesisSource {
    AUTOMATIC,
    USER_DIRECTED
}

/**
 * Lifecycle status of a research hypothesis.
 */
enum class ResearchHypothesisStatus {
    PROPOSED,
    READY_FOR_EXPERIMENT,
    UNDER_TEST,
    SUPPORTED,
    CONDITIONALLY_SUPPORTED,
    INCONCLUSIVE,
    FRAGILE,
    UNSUPPORTED,
    INVALIDATED,
    REQUIRES_FURTHER_VALIDATION
}
