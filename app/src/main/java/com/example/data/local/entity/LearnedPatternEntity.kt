package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learned_patterns")
data class LearnedPatternEntity(
    @PrimaryKey val id: String,
    val patternName: String,
    val regime: String,
    val sampleSize: Int,
    val winRatePercent: Double,
    val avgProfitFactor: Double,
    val confidenceScore: Double, // 0.0 to 1.0
    val keyLesson: String,
    val lastUpdated: Long
)
