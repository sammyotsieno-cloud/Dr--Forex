package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.research.ResearchHypothesis
import com.drforex.researchlab.core.research.ResearchQuestion
import com.drforex.researchlab.core.research.ResearchQuestionEngine

/**
 * Validates the structural and temporal integrity of a research experiment.
 *
 * ExperimentValidator is the validation boundary between:
 * - research definition
 * - experiment definition
 * - experiment execution
 * - experiment results
 *
 * Upstream research-question and hypothesis validation remains owned by
 * ResearchQuestionEngine. ExperimentValidator delegates that validation
 * rather than duplicating its rules.
 *
 * It does not:
 * - execute an experiment
 * - load market data
 * - calculate indicators
 * - calculate statistical significance
 * - evaluate profitability
 * - determine whether a hypothesis is supported
 * - generate trading signals
 * - produce forecasts
 */
class ExperimentValidator(
    private val researchQuestionEngine: ResearchQuestionEngine =
        ResearchQuestionEngine()
) {

    /**
     * Validates the complete experiment definition.
     *
     * This includes:
     * - research question and hypothesis validity
     * - question/hypothesis/experiment relationships
     * - hypothesis variable coverage
     * - experiment conditions
     * - experiment design
     * - temporal data boundaries
     * - information cutoff
     * - procedure structure
     */
    fun validateDefinition(
        question: ResearchQuestion,
        hypothesis: ResearchHypothesis,
        experiment: ResearchExperiment,
        design: ExperimentDesign,
        dataScope: ExperimentDataScope,
        procedure: ExperimentProcedure
    ): ExperimentValidation {

        val errors = mutableListOf<String>()

        val researchDefinitionValidation =
            researchQuestionEngine.validate(
                question = question,
                hypothesis = hypothesis
            )

        if (
            researchDefinitionValidation is
            ResearchQuestionEngine.ResearchDefinitionValidation.Invalid
        ) {
            errors += researchDefinitionValidation.errors
        }

        if (experiment.questionId != question.id) {
            errors +=
                "Experiment question id must match the supplied research question."
        }

        if (experiment.hypothesisId != hypothesis.id) {
            errors +=
                "Experiment hypothesis id must match the supplied research hypothesis."
        }

        if (hypothesis.questionId != question.id) {
            errors +=
                "Hypothesis question id must match the supplied research question."
        }

        val experimentVariableNames =
            experiment.variables.map { it.name }.toSet()

        val requiredIndependentVariables =
            hypothesis.independentVariables.toSet()

        val missingIndependentVariables =
            requiredIndependentVariables - experimentVariableNames

        if (missingIndependentVariables.isNotEmpty()) {
            errors +=
                "Experiment is missing hypothesis independent variables: " +
                    missingIndependentVariables.joinToString(", ")
        }

        if (hypothesis.dependentVariable !in experimentVariableNames) {
            errors +=
                "Experiment is missing the hypothesis dependent variable: " +
                    hypothesis.dependentVariable
        }

        val conditionNames =
            experiment.conditions.map { it.name }

        if (conditionNames.size != conditionNames.toSet().size) {
            errors +=
                "Experiment condition names must be unique."
        }

        if (design.repetitions <= 0) {
            errors +=
                "Experiment repetitions must be greater than zero."
        }

        if (
            design.comparison != ExperimentComparison.NONE &&
            design.baseline == null
        ) {
            errors +=
                "A baseline is required when the experiment uses a comparison."
        }

        if (dataScope.startTime.instant > dataScope.endTime.instant) {
            errors +=
                "Experiment data scope start time must not be after end time."
        }

        dataScope.informationCutoff?.let { cutoff ->

            if (cutoff.instant < dataScope.startTime.instant) {
                errors +=
                    "Information cutoff must not precede the experiment start."
            }

            if (cutoff.instant > dataScope.endTime.instant) {
                errors +=
                    "Information cutoff must not exceed the experiment end."
            }
        }

        if (procedure.steps.isEmpty()) {
            errors +=
                "Experiment procedure must contain at least one step."
        }

        val stepOrders =
            procedure.steps.map { it.order }

        if (stepOrders.size != stepOrders.toSet().size) {
            errors +=
                "Experiment procedure step orders must be unique."
        }

        if (stepOrders != stepOrders.sorted()) {
            errors +=
                "Experiment procedure steps must be in ascending order."
        }

        return if (errors.isEmpty()) {
            ExperimentValidation.Valid
        } else {
            ExperimentValidation.Invalid(errors)
        }
    }

    /**
     * Validates an experiment result against its declared data boundary.
     *
     * This protects the temporal integrity of experiment observations and
     * prevents observations from entering the result outside the declared
     * experiment scope or information cutoff.
     */
    fun validateResult(
        experiment: ResearchExperiment,
        dataScope: ExperimentDataScope,
        result: ExperimentResult
    ): ExperimentValidation {

        val errors = mutableListOf<String>()

        if (result.experimentId != experiment.id) {
            errors +=
                "Experiment result experimentId must match the experiment id."
        }

        if (
            result.completedAt != null &&
            result.completedAt.instant < result.startedAt.instant
        ) {
            errors +=
                "Experiment completion time must not precede experiment start."
        }

        val sequences =
            result.observations.map { it.sequence }

        if (sequences.size != sequences.toSet().size) {
            errors +=
                "Experiment observation sequence numbers must be unique."
        }

        if (sequences != sequences.sorted()) {
            errors +=
                "Experiment observations must be ordered by sequence number."
        }

        val observationTimes =
            result.observations.map { it.observedAt.instant }

        if (observationTimes != observationTimes.sorted()) {
            errors +=
                "Experiment observations must be ordered chronologically."
        }

        result.observations.forEach { observation ->

            if (
                observation.observedAt.instant <
                dataScope.startTime.instant
            ) {
                errors +=
                    "Experiment observation ${observation.sequence} " +
                        "occurs before the data scope."
            }

            if (
                observation.observedAt.instant >
                dataScope.endTime.instant
            ) {
                errors +=
                    "Experiment observation ${observation.sequence} " +
                        "occurs after the data scope."
            }

            dataScope.informationCutoff?.let { cutoff ->

                if (
                    observation.observedAt.instant >
                    cutoff.instant
                ) {
                    errors +=
                        "Experiment observation ${observation.sequence} " +
                            "occurs after the information cutoff."
                }
            }
        }

        if (
            result.status == ExperimentResultStatus.COMPLETED &&
            result.completedAt == null
        ) {
            errors +=
                "A completed experiment result must have a completion time."
        }

        if (
            result.status == ExperimentResultStatus.FAILED &&
            result.errors.isEmpty()
        ) {
            errors +=
                "A failed experiment result must contain at least one error."
        }

        return if (errors.isEmpty()) {
            ExperimentValidation.Valid
        } else {
            ExperimentValidation.Invalid(errors)
        }
    }
}

/**
 * Result of experiment validation.
 */
sealed class ExperimentValidation {

    /**
     * The experiment satisfies all validation rules.
     */
    data object Valid : ExperimentValidation()

    /**
     * The experiment violates one or more validation rules.
     */
    data class Invalid(
        val errors: List<String>
    ) : ExperimentValidation() {

        init {
            require(errors.isNotEmpty()) {
                "Invalid experiment validation must contain at least one error."
            }

            require(errors.all { it.isNotBlank() }) {
                "Experiment validation errors must not contain blank values."
            }
        }
    }
}
