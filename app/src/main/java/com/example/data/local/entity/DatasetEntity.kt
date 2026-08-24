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
    val validationSummary: String
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
            validationSummary = validationSummary
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
                validationSummary = meta.validationSummary
            )
        }
    }
}
