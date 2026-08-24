package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ExperimentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperimentDao {
    @Query("SELECT * FROM experiments ORDER BY timestamp DESC")
    fun getAllExperiments(): Flow<List<ExperimentEntity>>

    @Query("SELECT * FROM experiments WHERE experimentId = :id")
    suspend fun getExperimentById(id: String): ExperimentEntity?

    @Query("SELECT COUNT(*) FROM experiments")
    suspend fun getExperimentCount(): Int

    @Query("SELECT experimentId FROM experiments ORDER BY experimentId DESC LIMIT 1")
    suspend fun getLatestExperimentId(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiment(experiment: ExperimentEntity)

    @Query("DELETE FROM experiments WHERE experimentId = :id")
    suspend fun deleteExperimentById(id: String)

    @Query("DELETE FROM experiments")
    suspend fun deleteAllExperiments()
}
