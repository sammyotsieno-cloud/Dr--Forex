package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.research.ResearchHypothesis
import com.drforex.researchlab.core.research.ResearchQuestion

/**
 * Orchestrates the lifecycle of a defined research experiment.
 *
 * Validation rules are owned by ExperimentValidator.
 * The engine coordinates validation and procedure ordering but does not
 * maintain a second validation implementation.
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
 */
class ExperimentEngine(
    private val experimentValidator: ExperimentValidator =
        ExperimentValidator()
) {

    fun validateDefinition(
        question: ResearchQuestion,
        hypothesis: ResearchHypothesis,
        experiment: ResearchExperiment,
        design: ExperimentDesign,
        dataScope: ExperimentDataScope,
        procedure: ExperimentProcedure
    ): ExperimentValidation {
        return experimentValidator.validateDefinition(
            question = question,
            hypothesis = hypothesis,
            experiment = experiment,
            design = design,
            dataScope = dataScope,
            procedure = procedure
        )
    }

    fun orderedSteps(
        procedure: ExperimentProcedure
    ): List<ExperimentStep> {
        return procedure.steps.sortedBy { it.order }
    }

    fun canExecute(
        validation: ExperimentValidation
    ): Boolean {
        return validation is ExperimentValidation.Valid
    }
}
