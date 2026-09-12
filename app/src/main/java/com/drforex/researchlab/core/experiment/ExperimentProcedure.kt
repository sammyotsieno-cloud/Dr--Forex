package com.drforex.researchlab.core.experiment

/**
 * Defines the ordered procedure for conducting a research experiment.
 *
 * ExperimentProcedure describes what must happen during an experiment,
 * without performing those actions itself.
 *
 * The procedure is intentionally separate from ExperimentEngine:
 * - Procedure = what should happen and in what order.
 * - Engine = performs the procedure.
 *
 * It does not:
 * - access market data
 * - calculate indicators
 * - execute market trades
 * - calculate results
 * - determine hypothesis support
 * - generate trading signals
 */
data class ExperimentProcedure(
    val steps: List<ExperimentStep>,
    val requireSequentialExecution: Boolean = true,
    val stopOnFailure: Boolean = true
) {
    init {
        require(steps.isNotEmpty()) {
            "Experiment procedure must contain at least one step."
        }

        require(steps.map { it.order }.distinct().size == steps.size) {
            "Experiment procedure step orders must be unique."
        }

        require(steps.map { it.name }.distinct().size == steps.size) {
            "Experiment procedure step names must be unique."
        }

        require(steps.map { it.order }.sorted() == steps.map { it.order }) {
            "Experiment procedure steps must be ordered by ascending order."
        }
    }
}

/**
 * A single ordered action in an experiment procedure.
 *
 * This is a declarative description of an action. The procedure does not
 * execute it.
 */
data class ExperimentStep(
    val order: Int,
    val name: String,
    val description: String,
    val action: ExperimentAction,
    val required: Boolean = true
) {
    init {
        require(order > 0) {
            "Experiment procedure step order must be greater than zero."
        }

        require(name.isNotBlank()) {
            "Experiment procedure step name must not be blank."
        }

        require(description.isNotBlank()) {
            "Experiment procedure step description must not be blank."
        }
    }
}

/**
 * Defines the methodological action represented by an experiment step.
 *
 * These actions describe the research process, not implementation details.
 */
enum class ExperimentAction {

    /**
     * Establish or verify the observations that will be used.
     */
    PREPARE_OBSERVATIONS,

    /**
     * Apply the experiment's predefined conditions.
     */
    APPLY_CONDITIONS,

    /**
     * Evaluate the independent variables for the current observation.
     */
    MEASURE_INDEPENDENT_VARIABLES,

    /**
     * Observe or measure the dependent variable.
     */
    MEASURE_DEPENDENT_VARIABLE,

    /**
     * Record the observation produced by the procedure.
     */
    RECORD_OBSERVATION,

    /**
     * Repeat the defined procedure for another observation or trial.
     */
    REPEAT_TRIAL,

    /**
     * Complete the procedural collection phase without interpreting results.
     */
    COMPLETE_COLLECTION
}
