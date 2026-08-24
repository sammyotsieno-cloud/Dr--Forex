package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DatasetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatasetDao {
    @Query("SELECT * FROM datasets ORDER BY startDate DESC")
    fun getAllDatasets(): Flow<List<DatasetEntity>>

    @Query("SELECT * FROM datasets WHERE datasetId = :id")
    suspend fun getDatasetById(id: String): DatasetEntity?

    @Query("SELECT COUNT(*) FROM datasets")
    suspend fun getDatasetCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataset(dataset: DatasetEntity)

    @Query("DELETE FROM datasets WHERE datasetId = :id")
    suspend fun deleteDatasetById(id: String)

    @Query("DELETE FROM datasets")
    suspend fun deleteAllDatasets()
}
