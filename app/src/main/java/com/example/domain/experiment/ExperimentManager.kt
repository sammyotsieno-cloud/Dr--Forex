package com.example.domain.experiment

import com.example.data.repository.ExperimentRepository
import com.example.domain.model.Experiment
import com.example.domain.model.ExperimentStatus
import com.example.domain.model.PerformanceMetrics
import com.example.domain.model.ResearchConfiguration
import kotlinx.coroutines.flow.Flow
import java.util.Locale

/**
 * Manages experiment lifecycles, sequential ID generation (EXP-0001, EXP-0002, etc.),
 * and immutable local audit persistence.
 */
class ExperimentManager(
    private val experimentRepository: ExperimentRepository
) {

    val allExperiments: Flow<List<Experiment>> = experimentRepository.allExperiments

    /**
     * Generates next sequential experiment identifier.
     * e.g. "EXP-0001", "EXP-0002", "EXP-0099" -> "EXP-0100"
     */
    suspend fun generateNextExperimentId(): String {
        val latestId = experimentRepository.getLatestExperimentId()
        val nextNumber = if (latestId != null && latestId.startsWith("EXP-")) {
            val numPart = latestId.substringAfter("EXP-").toIntOrNull() ?: 0
            numPart + 1
        } else {
            val count = experimentRepository.getExperimentCount()
            count + 1
        }
        return formatExperimentId(nextNumber)
    }

    fun formatExperimentId(number: Int): String {
        return String.format(Locale.US, "EXP-%04d", number)
    }

    /**
     * Creates and persists a new research experiment record.
     */
    suspend fun createExperiment(
        strategyId: String,
        strategyName: String,
        datasetId: String,
        datasetName: String,
        instrument: String,
        timeframe: String,
        parametersSummary: String,
        notes: String = "",
        config: ResearchConfiguration = ResearchConfiguration(),
        status: ExperimentStatus = ExperimentStatus.CONFIGURED,
        metrics: PerformanceMetrics? = null
    ): Experiment {
        val experimentId = generateNextExperimentId()
        val experiment = Experiment(
            experimentId = experimentId,
            timestamp = System.currentTimeMillis(),
            strategyId = strategyId,
            strategyName = strategyName,
            datasetId = datasetId,
            datasetName = datasetName,
            instrument = instrument,
            timeframe = timeframe,
            parametersSummary = parametersSummary,
            status = status,
            metrics = metrics,
            notes = notes,
            initialCapital = config.initialCapital,
            riskPerTradePercent = config.defaultRiskPerTradePercent,
            spreadPips = config.defaultSpreadPips,
            slippagePips = config.defaultSlippagePips
        )

        experimentRepository.insertExperiment(experiment)
        return experiment
    }

    suspend fun deleteExperiment(id: String) {
        experimentRepository.deleteExperimentById(id)
    }

    suspend fun getExperiment(id: String): Experiment? {
        return experimentRepository.getExperimentById(id)
    }
}
