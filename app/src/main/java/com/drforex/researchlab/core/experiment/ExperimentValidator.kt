package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.research.ResearchHypothesis
import com.drforex.researchlab.core.research.ResearchQuestion

/**
 * Validates the structural and temporal integrity of a research experiment.
 *
 * ExperimentValidator is a safety boundary between experiment definition,
 * execution, and later analysis.
 *
 * It does not:
 * - execute an experiment
 * - calculate indicators
 * - calculate statistical significance
 * - evaluate profitability
 * - determine whether a hypothesis is supported
 * - generate trading signals
 * - produce forecasts
 */
class ExperimentValidator {

    /**
     * Validates the complete experiment definition.
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

        val experimentVariables =
            experiment.variables.map { it.name }.toSet()

        if (!experimentVariables.containsAll(hypothesis.independentVariables)) {
            errors +=
                "Experiment must include all independent variables declared by the hypothesis."
        }

        if (hypothesis.dependentVariable !in experimentVariables) {
            errors +=
                "Experiment must include the dependent variable declared by the hypothesis."
        }

        if (experiment.conditions.map { it.name }.distinct().size !=
            experiment.conditions.size
        ) {
            errors +=
                "Experiment conditions must have unique names."
        }

        if (design.repetitions <= 0) {
            errors +=
                "Experiment repetitions must be greater than zero."
        }

        if (design.comparison != ExperimentComparison.NONE &&
            design.baseline == null
        ) {
            errors +=
                "A comparison design must define a baseline."
        }

        if (dataScope.startTime.instant > dataScope.endTime.instant) {
            errors +=
                "Experiment data scope start time must not be after end time."
        }

        if (dataScope.informationCutoff != null) {
            if (dataScope.informationCutoff.instant <
                dataScope.startTime.instant
            ) {
                errors +=
                    "Information cutoff must not precede the experiment start."
            }

            if (dataScope.informationCutoff.instant >
                dataScope.endTime.instant
            ) {
                errors +=
                    "Information cutoff must not exceed the experiment end."
            }
        }

        if (procedure.steps.isEmpty()) {
            errors +=
                "Experiment procedure must contain at least one step."
        }

        if (procedure.steps.map { it.order }.distinct().size !=
            procedure.steps.size
        ) {
            errors +=
                "Experiment procedure step orders must be unique."
        }

        if (procedure.steps.map { it.order }.sorted() !=
            procedure.steps.map { it.order }
        ) {
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
     * Observations must remain inside the experiment's permitted temporal
     * scope. This prevents a result from containing observations outside
     * the experiment definition.
     */
    fun validateResult(
        experiment: ResearchExperiment,
        dataScope: ExperimentDataScope,
        result: ExperimentResult
    ): ExperimentValidation {
        val errors = mutableListOf<String>()

        if (result.experimentId != experiment.id) {
            errors +=
                "Experiment result id must match the supplied experiment."
        }

        if (result.completedAt != null &&
            result.completedAt.instant < result.startedAt.instant
        ) {
            errors +=
                "Experiment completion time must not precede experiment start."
        }

        val observations = result.observations

        val sequences = observations.map { it.sequence }

        if (sequences.distinct().size != sequences.size) {
            errors +=
                "Experiment observation sequence numbers must be unique."
        }

        if (sequences != sequences.sorted()) {
            errors +=
                "Experiment observations must be ordered by sequence."
        }

        val observationTimes =
            observations.map { it.observedAt.instant }

        if (observationTimes != observationTimes.sorted()) {
            errors +=
                "Experiment observations must be in chronological order."
        }

        observations.forEach { observation ->
            if (observation.observedAt.instant <
                dataScope.startTime.instant
            ) {
                errors +=
                    "Experiment observation occurs before the data scope."
            }

            if (observation.observedAt.instant >
                dataScope.endTime.instant
            ) {
                errors +=
                    "Experiment observation occurs after the data scope."
            }

            val cutoff = dataScope.informationCutoff

            if (cutoff != null &&
                observation.observedAt.instant > cutoff.instant
            ) {
                errors +=
                    "Experiment observation occurs after the information cutoff."
            }
        }

        if (result.status == ExperimentResultStatus.COMPLETED &&
            result.completedAt == null
        ) {
            errors +=
                "A completed experiment result must have a completion time."
        }

        if (result.status == ExperimentResultStatus.FAILED &&
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
     * The experiment satisfies the validation rules.
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
