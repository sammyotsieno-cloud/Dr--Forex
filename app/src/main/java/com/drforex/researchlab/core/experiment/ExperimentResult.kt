package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.time.MarketTime

/**
 * Represents the result produced by a completed or partially completed
 * research experiment.
 *
 * ExperimentResult records what the experiment produced. It does not
 * determine whether the underlying hypothesis is true or supported.
 *
 * It does not:
 * - determine statistical significance
 * - evaluate profitability
 * - approve a strategy
 * - generate a trading signal
 * - produce a forecast
 * - classify a hypothesis as supported or unsupported
 */
data class ExperimentResult(
    val experimentId: String,
    val startedAt: MarketTime,
    val completedAt: MarketTime?,
    val status: ExperimentResultStatus,
    val observations: List<ExperimentObservation> = emptyList(),
    val executionNotes: List<String> = emptyList(),
    val errors: List<String> = emptyList()
) {
    init {
        require(experimentId.isNotBlank()) {
            "Experiment result experiment id must not be blank."
        }

        if (completedAt != null) {
            require(completedAt.instant >= startedAt.instant) {
                "Experiment completion time must not be before its start time."
            }
        }

        require(executionNotes.all { it.isNotBlank() }) {
            "Experiment execution notes must not contain blank values."
        }

        require(errors.all { it.isNotBlank() }) {
            "Experiment errors must not contain blank values."
        }
    }
}

/**
 * A single observation produced during experiment execution.
 *
 * An observation contains recorded values rather than an interpretation
 * of those values.
 */
data class ExperimentObservation(
    val sequence: Int,
    val observedAt: MarketTime,
    val values: Map<String, Double>,
    val conditionState: Map<String, Boolean> = emptyMap()
) {
    init {
        require(sequence > 0) {
            "Experiment observation sequence must be greater than zero."
        }

        require(values.isNotEmpty()) {
            "Experiment observation must contain at least one value."
        }

        require(values.keys.all { it.isNotBlank() }) {
            "Experiment observation variable names must not be blank."
        }

        require(values.values.all { it.isFinite() }) {
            "Experiment observation values must be finite."
        }

        require(conditionState.keys.all { it.isNotBlank() }) {
            "Experiment observation condition names must not be blank."
        }
    }
}

/**
 * Lifecycle state of an experiment result.
 *
 * These states describe what happened during experiment execution.
 * They do not describe whether the research hypothesis was supported.
 */
enum class ExperimentResultStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    PARTIALLY_COMPLETED,
    FAILED,
    CANCELLED
}
