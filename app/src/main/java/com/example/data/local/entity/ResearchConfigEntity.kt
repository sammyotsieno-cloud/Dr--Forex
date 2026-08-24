package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.ResearchConfiguration

@Entity(tableName = "research_config")
data class ResearchConfigEntity(
    @PrimaryKey val id: Int = 1,
    val baseCurrency: String,
    val initialCapital: Double,
    val defaultRiskPerTradePercent: Double,
    val defaultInstrument: String,
    val defaultExecutionTimeframe: String,
    val contextTimeframesCsv: String,
    val defaultSpreadPips: Double,
    val defaultSlippagePips: Double,
    val maxDrawdownLimitPercent: Double
) {
    fun toDomain(): ResearchConfiguration {
        val timeframes = contextTimeframesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return ResearchConfiguration(
            baseCurrency = baseCurrency,
            initialCapital = initialCapital,
            defaultRiskPerTradePercent = defaultRiskPerTradePercent,
            defaultInstrument = defaultInstrument,
            defaultExecutionTimeframe = defaultExecutionTimeframe,
            contextTimeframes = if (timeframes.isEmpty()) listOf("H1", "H4") else timeframes,
            defaultSpreadPips = defaultSpreadPips,
            defaultSlippagePips = defaultSlippagePips,
            maxDrawdownLimitPercent = maxDrawdownLimitPercent
        )
    }

    companion object {
        fun fromDomain(config: ResearchConfiguration): ResearchConfigEntity {
            return ResearchConfigEntity(
                id = 1,
                baseCurrency = config.baseCurrency,
                initialCapital = config.initialCapital,
                defaultRiskPerTradePercent = config.defaultRiskPerTradePercent,
                defaultInstrument = config.defaultInstrument,
                defaultExecutionTimeframe = config.defaultExecutionTimeframe,
                contextTimeframesCsv = config.contextTimeframes.joinToString(","),
                defaultSpreadPips = config.defaultSpreadPips,
                defaultSlippagePips = config.defaultSlippagePips,
                maxDrawdownLimitPercent = config.maxDrawdownLimitPercent
            )
        }
    }
}
