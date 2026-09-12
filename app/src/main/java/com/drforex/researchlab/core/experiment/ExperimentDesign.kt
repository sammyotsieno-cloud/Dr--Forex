package com.drforex.researchlab.core.experiment

/**
 * Defines the methodological structure used to test a research experiment.
 *
 * ExperimentDesign describes how observations will be organized and compared.
 * It does not define the actual market-data range or execute the experiment.
 *
 * It does not:
 * - access market data
 * - calculate indicators
 * - execute an experiment
 * - perform a backtest
 * - calculate statistical results
 * - determine whether a hypothesis is supported
 *
 * Data boundaries belong to ExperimentDataScope.
 * Execution steps belong to ExperimentProcedure.
 */
data class ExperimentDesign(
    val designType: ExperimentDesignType,
    val observationUnit: ObservationUnit,
    val comparison: ExperimentComparison = ExperimentComparison.NONE,
    val grouping: ExperimentGrouping = ExperimentGrouping.NONE,
    val baseline: ExperimentBaseline? = null,
    val repetitions: Int = 1,
    val preserveObservationOrder: Boolean = true,
    val constraints: List<ExperimentDesignConstraint> = emptyList()
) {
    init {
        require(repetitions > 0) {
            "Experiment repetitions must be greater than zero."
        }

        require(constraints.distinct().size == constraints.size) {
            "Experiment design constraints must be unique."
        }

        if (comparison != ExperimentComparison.NONE) {
            require(baseline != null) {
                "A comparison design must define a baseline."
            }
        }
    }
}

/**
 * High-level methodological structure of an experiment.
 */
enum class ExperimentDesignType {
    OBSERVATIONAL,
    COMPARATIVE,
    CONTROLLED,
    CONDITIONAL,
    PREDICTIVE,
    REPEATED_OBSERVATION
}

/**
 * Defines the unit being observed by the experiment.
 *
 * This describes what constitutes one observation.
 */
enum class ObservationUnit {
    CANDLE,
    TIME_WINDOW,
    MARKET_SESSION,
    MARKET_REGIME,
    EVENT,
    TRADE_OPPORTUNITY,
    EXPERIMENT_TRIAL
}

/**
 * Defines how observations are compared.
 */
enum class ExperimentComparison {
    NONE,
    BEFORE_AND_AFTER,
    GROUP_A_AND_GROUP_B,
    CONDITION_PRESENT_AND_ABSENT,
    REGIME_A_AND_REGIME_B,
    BASELINE_AND_TEST
}

/**
 * Defines how observations may be grouped for analysis.
 */
enum class ExperimentGrouping {
    NONE,
    TIMEFRAME,
    MARKET_REGIME,
    SESSION,
    VOLATILITY_STATE,
    TREND_STATE,
    CONDITION,
    CUSTOM
}

/**
 * Defines the reference against which an experimental condition is compared.
 */
data class ExperimentBaseline(
    val name: String,
    val description: String
) {
    init {
        require(name.isNotBlank()) {
            "Experiment baseline name must not be blank."
        }

        require(description.isNotBlank()) {
            "Experiment baseline description must not be blank."
        }
    }
}

/**
 * A methodological constraint that must remain true while the experiment
 * is designed or executed.
 *
 * Examples include:
 * - no future information
 * - preserve chronological order
 * - use closed candles only
 * - keep a specified condition unchanged
 */
data class ExperimentDesignConstraint(
    val name: String,
    val description: String
) {
    init {
        require(name.isNotBlank()) {
            "Experiment design constraint name must not be blank."
        }

        require(description.isNotBlank()) {
            "Experiment design constraint description must not be blank."
        }
    }
}
