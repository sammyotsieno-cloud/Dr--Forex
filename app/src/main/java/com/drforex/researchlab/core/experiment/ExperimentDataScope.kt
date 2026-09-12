package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.time.MarketTime

/**
 * Defines the market-data boundary available to a research experiment.
 *
 * ExperimentDataScope specifies what information an experiment is permitted
 * to use. It does not load, validate, transform, or analyze market data.
 *
 * The scope exists to establish an explicit information boundary before
 * experiment execution begins.
 *
 * It does not:
 * - access market data
 * - load files or databases
 * - calculate indicators
 * - execute an experiment
 * - perform a backtest
 * - produce results
 *
 * Later execution components must not use information outside this scope.
 */
data class ExperimentDataScope(
    val instrument: String,
    val timeframe: String,
    val startTime: MarketTime,
    val endTime: MarketTime,
    val informationCutoff: MarketTime? = null,
    val dataPolicy: ExperimentDataPolicy = ExperimentDataPolicy.CLOSED_CANDLE_ONLY
) {
    init {
        require(instrument.isNotBlank()) {
            "Experiment data scope instrument must not be blank."
        }

        require(timeframe.isNotBlank()) {
            "Experiment data scope timeframe must not be blank."
        }

        require(startTime.instant <= endTime.instant) {
            "Experiment data scope start time must not be after end time."
        }

        if (informationCutoff != null) {
            require(informationCutoff.instant >= startTime.instant) {
                "Information cutoff must not be before the experiment start time."
            }

            require(informationCutoff.instant <= endTime.instant) {
                "Information cutoff must not be after the experiment end time."
            }
        }
    }
}

/**
 * Defines the information-access policy for experiment data.
 */
enum class ExperimentDataPolicy {

    /**
     * Only information available from completed candles may be used.
     */
    CLOSED_CANDLE_ONLY,

    /**
     * The experiment must preserve chronological observation order.
     */
    CHRONOLOGICAL,

    /**
     * The experiment may use only information available at each observation
     * point in time.
     */
    POINT_IN_TIME,

    /**
     * The experiment must preserve chronological order and enforce
     * point-in-time information availability.
     */
    CHRONOLOGICAL_POINT_IN_TIME
}
