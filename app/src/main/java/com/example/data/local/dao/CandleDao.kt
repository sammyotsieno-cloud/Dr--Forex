package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CandleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CandleDao {

    @Query("SELECT * FROM market_candles WHERE datasetId = :datasetId ORDER BY timestamp ASC")
    fun getCandlesByDatasetId(datasetId: String): Flow<List<CandleEntity>>

    @Query("SELECT * FROM market_candles WHERE datasetId = :datasetId ORDER BY timestamp ASC")
    suspend fun getCandlesListByDatasetId(datasetId: String): List<CandleEntity>

    @Query("SELECT COUNT(*) FROM market_candles WHERE datasetId = :datasetId")
    suspend fun getCandleCountByDatasetId(datasetId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCandles(candles: List<CandleEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCandles(candles: List<CandleEntity>)

    @Query("DELETE FROM market_candles WHERE datasetId = :datasetId")
    suspend fun deleteCandlesByDatasetId(datasetId: String)

    @Query("DELETE FROM market_candles")
    suspend fun deleteAllCandles()
}
