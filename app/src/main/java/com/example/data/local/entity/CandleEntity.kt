package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.example.domain.model.MarketCandle

@Entity(
    tableName = "market_candles",
    primaryKeys = ["datasetId", "timestamp"],
    indices = [
        Index(value = ["datasetId"]),
        Index(value = ["datasetId", "timestamp"], unique = true)
    ]
)
data class CandleEntity(
    val datasetId: String,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double = 0.0
) {
    fun toDomain(): MarketCandle {
        return MarketCandle(
            timestamp = timestamp,
            open = open,
            high = high,
            low = low,
            close = close,
            volume = volume
        )
    }

    companion object {
        fun fromDomain(datasetId: String, candle: MarketCandle): CandleEntity {
            return CandleEntity(
                datasetId = datasetId,
                timestamp = candle.timestamp,
                open = candle.open,
                high = candle.high,
                low = candle.low,
                close = candle.close,
                volume = candle.volume
            )
        }
    }
}
