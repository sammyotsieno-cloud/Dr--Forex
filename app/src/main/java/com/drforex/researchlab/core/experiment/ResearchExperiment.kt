package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.research.ResearchCondition
import com.drforex.researchlab.core.time.MarketTime

/**
 * A formally defined research experiment derived from a research hypothesis.
 *
 * A ResearchExperiment describes what is intended to be tested and provides
 * the structural definition required by later experiment infrastructure.
 *
 * It does not:
 * - execute an experiment
 * - access market data
 * - perform a backtest
 * - calculate statistical results
 * - generate a trading signal
 * - evaluate profitability
 * - determine whether a hypothesis is supported
 *
 * Experiment execution belongs to the ExperimentEngine.
 * Experiment design, data scope, and procedure are defined by later
 * experiment components.
 */
data class ResearchExperiment(
    val id: String,
    val questionId: String,
    val hypothesisId: String,
    val title: String,
    val description: String,
    val createdAt: MarketTime,
    val type: ExperimentType,
    val variables: List<ExperimentVariable> = emptyList(),
    val conditions: List<ResearchCondition> = emptyList(),
    val expectedOutcome: String,
    val assumptions: List<String> = emptyList(),
    val status: ResearchExperimentStatus = ResearchExperimentStatus.DRAFT
) {
    init {
        require(id.isNotBlank()) {
            "Research experiment id must not be blank."
        }

        require(questionId.isNotBlank()) {
            "Research experiment question id must not be blank."
        }

        require(hypothesisId.isNotBlank()) {
            "Research experiment hypothesis id must not be blank."
        }

        require(title.isNotBlank()) {
            "Research experiment title must not be blank."
        }

        require(description.isNotBlank()) {
            "Research experiment description must not be blank."
        }

        require(expectedOutcome.isNotBlank()) {
            "Research experiment must define an expected outcome."
        }

        require(variables.map { it.name }.distinct().size == variables.size) {
            "Research experiment variables must have unique names."
        }

        require(conditions.map { it.name }.distinct().size == conditions.size) {
            "Research experiment conditions must have unique names."
        }

        require(assumptions.all { it.isNotBlank() }) {
            "Research experiment assumptions must not contain blank values."
        }
    }
}

/**
 * Describes the methodological category of a research experiment.
 *
 * These values describe the experiment design only.
 * They do not imply that any result will be produced or that a hypothesis
 * will be supported.
 */
enum class ExperimentType {
    OBSERVATIONAL,
    COMPARATIVE,
    ASSOCIATION,
    CONDITIONAL,
    PREDICTIVE,
    CONTROLLED
}

/**
 * A variable explicitly included in an experiment.
 *
 * The experiment records the variable and its role but does not calculate
 * or evaluate it.
 */
data class ExperimentVariable(
    val name: String,
    val role: ExperimentVariableRole,
    val description: String
) {
    init {
        require(name.isNotBlank()) {
            "Experiment variable name must not be blank."
        }

        require(description.isNotBlank()) {
            "Experiment variable description must not be blank."
        }
    }
}

/**
 * Defines how a variable participates in an experiment.
 */
enum class ExperimentVariableRole {
    INDEPENDENT,
    DEPENDENT,
    CONTROL,
    OBSERVATIONAL,
    CONTEXT
}

/**
 * Lifecycle state of a research experiment.
 *
 * Lifecycle state describes where the experiment is in the research
 * process. It does not describe whether the underlying hypothesis is true.
 */
enum class ResearchExperimentStatus {
    DRAFT,
    READY,
    RUNNING,
    COMPLETED,
    INCONCLUSIVE,
    INVALIDATED,
    CANCELLED
}
