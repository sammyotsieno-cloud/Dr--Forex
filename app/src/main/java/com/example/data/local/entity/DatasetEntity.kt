package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.ValidationStatus

@Entity(tableName = "datasets")
data class DatasetEntity(
    @PrimaryKey val datasetId: String,
    val name: String,
    val symbol: String,
    val timeframe: String,
    val startDate: Long,
    val endDate: Long,
    val rowCount: Int,
    val source: String,
    val timezone: String,
    val validationStatus: String,
    val isDevelopmentSample: Boolean,
    val validationSummary: String,
    val qualityScore: Int = 100,
    val qualityClassification: String = "Excellent",
    val gapCount: Int = 0,
    val priceSpikeCount: Int = 0,
    val flatlineCount: Int = 0
) {
    fun toDomain(): DatasetMetadata {
        val status = try {
            ValidationStatus.valueOf(validationStatus)
        } catch (e: Exception) {
            ValidationStatus.UNCHECKED
        }

        return DatasetMetadata(
            datasetId = datasetId,
            name = name,
            symbol = symbol,
            timeframe = timeframe,
            startDate = startDate,
            endDate = endDate,
            rowCount = rowCount,
            source = source,
            timezone = timezone,
            validationStatus = status,
            isDevelopmentSample = isDevelopmentSample,
            validationSummary = validationSummary,
            qualityScore = qualityScore,
            qualityClassification = qualityClassification,
            gapCount = gapCount,
            priceSpikeCount = priceSpikeCount,
            flatlineCount = flatlineCount
        )
    }

    companion object {
        fun fromDomain(meta: DatasetMetadata): DatasetEntity {
            return DatasetEntity(
                datasetId = meta.datasetId,
                name = meta.name,
                symbol = meta.symbol,
                timeframe = meta.timeframe,
                startDate = meta.startDate,
                endDate = meta.endDate,
                rowCount = meta.rowCount,
                source = meta.source,
                timezone = meta.timezone,
                validationStatus = meta.validationStatus.name,
                isDevelopmentSample = meta.isDevelopmentSample,
                validationSummary = meta.validationSummary,
                qualityScore = meta.qualityScore,
                qualityClassification = meta.qualityClassification,
                gapCount = meta.gapCount,
                priceSpikeCount = meta.priceSpikeCount,
                flatlineCount = meta.flatlineCount
            )
        }
    }
}
