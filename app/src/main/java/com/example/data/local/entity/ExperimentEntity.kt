package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Experiment
import com.example.domain.model.ExperimentStatus
import com.example.domain.model.PerformanceMetrics

@Entity(tableName = "experiments")
data class ExperimentEntity(
    @PrimaryKey val experimentId: String,
    val timestamp: Long,
    val strategyId: String,
    val strategyName: String,
    val datasetId: String,
    val datasetName: String,
    val instrument: String,
    val timeframe: String,
    val parametersSummary: String,
    val status: String,
    val notes: String,
    val initialCapital: Double,
    val riskPerTradePercent: Double,
    val spreadPips: Double,
    val slippagePips: Double,
    // Performance Metrics fields
    val netProfit: Double = 0.0,
    val returnPercent: Double = 0.0,
    val winRate: Double = 0.0,
    val profitFactor: Double = 0.0,
    val expectancy: Double = 0.0,
    val maximumDrawdown: Double = 0.0,
    val maximumDrawdownPercent: Double = 0.0,
    val recoveryFactor: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val sortinoRatio: Double = 0.0,
    val numberOfTrades: Int = 0
) {
    fun toDomain(): Experiment {
        val parsedStatus = try {
            ExperimentStatus.valueOf(status)
        } catch (e: Exception) {
            ExperimentStatus.CONFIGURED
        }

        val metrics = if (numberOfTrades > 0 || netProfit != 0.0) {
            PerformanceMetrics(
                netProfit = netProfit,
                returnPercent = returnPercent,
                winRate = winRate,
                profitFactor = profitFactor,
                expectancy = expectancy,
                maximumDrawdown = maximumDrawdown,
                maximumDrawdownPercent = maximumDrawdownPercent,
                recoveryFactor = recoveryFactor,
                sharpeRatio = sharpeRatio,
                sortinoRatio = sortinoRatio,
                numberOfTrades = numberOfTrades
            )
        } else null

        return Experiment(
            experimentId = experimentId,
            timestamp = timestamp,
            strategyId = strategyId,
            strategyName = strategyName,
            datasetId = datasetId,
            datasetName = datasetName,
            instrument = instrument,
            timeframe = timeframe,
            parametersSummary = parametersSummary,
            status = parsedStatus,
            metrics = metrics,
            notes = notes,
            initialCapital = initialCapital,
            riskPerTradePercent = riskPerTradePercent,
            spreadPips = spreadPips,
            slippagePips = slippagePips
        )
    }

    companion object {
        fun fromDomain(exp: Experiment): ExperimentEntity {
            return ExperimentEntity(
                experimentId = exp.experimentId,
                timestamp = exp.timestamp,
                strategyId = exp.strategyId,
                strategyName = exp.strategyName,
                datasetId = exp.datasetId,
                datasetName = exp.datasetName,
                instrument = exp.instrument,
                timeframe = exp.timeframe,
                parametersSummary = exp.parametersSummary,
                status = exp.status.name,
                notes = exp.notes,
                initialCapital = exp.initialCapital,
                riskPerTradePercent = exp.riskPerTradePercent,
                spreadPips = exp.spreadPips,
                slippagePips = exp.slippagePips,
                netProfit = exp.metrics?.netProfit ?: 0.0,
                returnPercent = exp.metrics?.returnPercent ?: 0.0,
                winRate = exp.metrics?.winRate ?: 0.0,
                profitFactor = exp.metrics?.profitFactor ?: 0.0,
                expectancy = exp.metrics?.expectancy ?: 0.0,
                maximumDrawdown = exp.metrics?.maximumDrawdown ?: 0.0,
                maximumDrawdownPercent = exp.metrics?.maximumDrawdownPercent ?: 0.0,
                recoveryFactor = exp.metrics?.recoveryFactor ?: 0.0,
                sharpeRatio = exp.metrics?.sharpeRatio ?: 0.0,
                sortinoRatio = exp.metrics?.sortinoRatio ?: 0.0,
                numberOfTrades = exp.metrics?.numberOfTrades ?: 0
            )
        }
    }
}
