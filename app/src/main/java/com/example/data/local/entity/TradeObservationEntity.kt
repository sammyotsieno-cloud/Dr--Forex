package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_observations")
data class TradeObservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val experimentId: String,
    val symbol: String,
    val tradeDirection: String,
    val entryPrice: Double,
    val exitPrice: Double,
    val pnl: Double,
    val outcome: String, // "WIN", "LOSS", "BREAKEVEN"
    val rootCause: String,
    val mfePips: Double, // Max Favorable Excursion
    val maePips: Double, // Max Adverse Excursion
    val regime: String,
    val timestamp: Long
)
