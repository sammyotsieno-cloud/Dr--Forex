package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.research.ResearchHypothesis
import com.drforex.researchlab.core.research.ResearchQuestion
import com.drforex.researchlab.core.research.ResearchQuestionEngine

/**
 * Orchestrates the lifecycle of a defined research experiment.
 *
 * ExperimentEngine is responsible for validating that the experiment has a
 * coherent research definition and for coordinating the declared experiment
 * procedure.
 *
 * It does not:
 * - load market data
 * - calculate indicators
 * - perform a backtest
 * - calculate statistical results
 * - generate trading signals
 * - evaluate profitability
 * - determine whether a hypothesis is supported
 * - modify research definitions
 *
 * Those responsibilities belong to later research components.
 */
class ExperimentEngine(
    private val researchQuestionEngine: ResearchQuestionEngine =
        ResearchQuestionEngine()
) {

    /**
     * Validates the complete research definition required before execution.
     *
     * The experiment must correspond to the supplied research question and
     * hypothesis before its procedure can be considered executable.
     */
    fun validateDefinition(
        question: ResearchQuestion,
        hypothesis: ResearchHypothesis,
        experiment: ResearchExperiment,
        design: ExperimentDesign,
        dataScope: ExperimentDataScope,
        procedure: ExperimentProcedure
    ): ExperimentDefinitionValidation {
        val researchValidation =
            researchQuestionEngine.validate(question, hypothesis)

        if (researchValidation is ResearchQuestionEngine.ResearchDefinitionValidation.Invalid) {
            return ExperimentDefinitionValidation.Invalid(
                researchValidation.errors
            )
        }

        if (experiment.questionId != question.id) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Experiment question id must match the supplied research question."
                )
            )
        }

        if (experiment.hypothesisId != hypothesis.id) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Experiment hypothesis id must match the supplied research hypothesis."
                )
            )
        }

        if (hypothesis.questionId != question.id) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Hypothesis question id must match the supplied research question."
                )
            )
        }

        val experimentVariableNames =
            experiment.variables.map { it.name }.toSet()

        val hypothesisIndependentVariables =
            hypothesis.independentVariables.toSet()

        if (!experimentVariableNames.containsAll(hypothesisIndependentVariables)) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Experiment must include all independent variables declared by the hypothesis."
                )
            )
        }

        if (!experimentVariableNames.contains(hypothesis.dependentVariable)) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Experiment must include the dependent variable declared by the hypothesis."
                )
            )
        }

        if (dataScope.startTime.instant > dataScope.endTime.instant) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Experiment data scope start time must not be after end time."
                )
            )
        }

        if (procedure.steps.isEmpty()) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Experiment procedure must contain at least one step."
                )
            )
        }

        if (design.repetitions <= 0) {
            return ExperimentDefinitionValidation.Invalid(
                listOf(
                    "Experiment design repetitions must be greater than zero."
                )
            )
        }

        return ExperimentDefinitionValidation.Valid
    }

    /**
     * Returns the procedure steps in their declared execution order.
     *
     * No experiment action is performed by this method.
     */
    fun orderedSteps(
        procedure: ExperimentProcedure
    ): List<ExperimentStep> {
        return procedure.steps.sortedBy { it.order }
    }

    /**
     * Determines whether the experiment definition is eligible to move
     * into execution.
     *
     * This method performs no execution.
     */
    fun canExecute(
        validation: ExperimentDefinitionValidation
    ): Boolean {
        return validation is ExperimentDefinitionValidation.Valid
    }
}

/**
 * Result of validating an experiment's complete structural definition.
 */
sealed class ExperimentDefinitionValidation {

    /**
     * The experiment definition is structurally coherent and may proceed
     * to an execution component.
     */
    data object Valid : ExperimentDefinitionValidation()

    /**
     * The experiment definition cannot proceed until the listed problems
     * have been resolved.
     */
    data class Invalid(
        val errors: List<String>
    ) : ExperimentDefinitionValidation() {
        init {
            require(errors.isNotEmpty()) {
                "Invalid experiment definition must contain at least one error."
            }

            require(errors.all { it.isNotBlank() }) {
                "Experiment definition errors must not contain blank values."
            }
        }
    }
}
