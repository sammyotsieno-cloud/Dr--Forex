package com.example.data.repository

import com.example.data.local.dao.CandleDao
import com.example.data.local.dao.DatasetDao
import com.example.data.local.entity.CandleEntity
import com.example.data.local.entity.DatasetEntity
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.MarketCandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

interface DatasetRepository {
    val allDatasets: Flow<List<DatasetMetadata>>
    suspend fun getDatasetById(id: String): DatasetMetadata?
    suspend fun insertDataset(dataset: DatasetMetadata)
    suspend fun deleteDatasetById(id: String)
    suspend fun getDatasetCount(): Int

    // Candle persistence
    suspend fun insertCandles(datasetId: String, candles: List<MarketCandle>): Pair<Int, Int>
    suspend fun getCandlesByDatasetId(datasetId: String): List<MarketCandle>
    fun getCandlesFlowByDatasetId(datasetId: String): Flow<List<MarketCandle>>
    suspend fun getCandleCountByDatasetId(datasetId: String): Int
}

class DatasetRepositoryImpl(
    private val datasetDao: DatasetDao,
    private val candleDao: CandleDao? = null
) : DatasetRepository {

    override val allDatasets: Flow<List<DatasetMetadata>> =
        datasetDao.getAllDatasets().map { list -> list.map { it.toDomain() } }

    override suspend fun getDatasetById(id: String): DatasetMetadata? {
        return datasetDao.getDatasetById(id)?.toDomain()
    }

    override suspend fun insertDataset(dataset: DatasetMetadata) {
        datasetDao.insertDataset(DatasetEntity.fromDomain(dataset))
    }

    override suspend fun deleteDatasetById(id: String) {
        candleDao?.deleteCandlesByDatasetId(id)
        datasetDao.deleteDatasetById(id)
    }

    override suspend fun getDatasetCount(): Int {
        return datasetDao.getDatasetCount()
    }

    override suspend fun insertCandles(datasetId: String, candles: List<MarketCandle>): Pair<Int, Int> {
        if (candleDao == null || candles.isEmpty()) return Pair(0, 0)
        val entities = candles.map { CandleEntity.fromDomain(datasetId, it) }
        val insertResults = candleDao.insertCandles(entities)
        val inserted = insertResults.count { it != -1L }
        val skipped = insertResults.count { it == -1L }
        return Pair(inserted, skipped)
    }

    override suspend fun getCandlesByDatasetId(datasetId: String): List<MarketCandle> {
        return candleDao?.getCandlesListByDatasetId(datasetId)?.map { it.toDomain() } ?: emptyList()
    }

    override fun getCandlesFlowByDatasetId(datasetId: String): Flow<List<MarketCandle>> {
        return candleDao?.getCandlesByDatasetId(datasetId)?.map { list -> list.map { it.toDomain() } }
            ?: emptyFlow()
    }

    override suspend fun getCandleCountByDatasetId(datasetId: String): Int {
        return candleDao?.getCandleCountByDatasetId(datasetId) ?: 0
    }
}

