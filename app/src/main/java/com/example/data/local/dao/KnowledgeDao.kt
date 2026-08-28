package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.LearnedPatternEntity
import com.example.data.local.entity.TradeObservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservation(observation: TradeObservationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservations(observations: List<TradeObservationEntity>)

    @Query("SELECT * FROM trade_observations ORDER BY timestamp DESC")
    fun getAllObservations(): Flow<List<TradeObservationEntity>>

    @Query("SELECT * FROM trade_observations ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentObservations(limit: Int): Flow<List<TradeObservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: LearnedPatternEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatterns(patterns: List<LearnedPatternEntity>)

    @Query("SELECT * FROM learned_patterns ORDER BY confidenceScore DESC")
    fun getAllPatterns(): Flow<List<LearnedPatternEntity>>

    @Query("SELECT * FROM learned_patterns WHERE confidenceScore >= :minConfidence ORDER BY confidenceScore DESC")
    fun getTopPatterns(minConfidence: Double): Flow<List<LearnedPatternEntity>>

    @Query("SELECT COUNT(*) FROM trade_observations")
    suspend fun getObservationCount(): Int

    @Query("SELECT COUNT(*) FROM learned_patterns")
    suspend fun getPatternCount(): Int
}
