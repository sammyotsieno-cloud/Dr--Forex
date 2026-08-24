package com.example.data.repository

import com.example.data.local.dao.DatasetDao
import com.example.data.local.entity.DatasetEntity
import com.example.domain.model.DatasetMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DatasetRepository {
    val allDatasets: Flow<List<DatasetMetadata>>
    suspend fun getDatasetById(id: String): DatasetMetadata?
    suspend fun insertDataset(dataset: DatasetMetadata)
    suspend fun deleteDatasetById(id: String)
    suspend fun getDatasetCount(): Int
}

class DatasetRepositoryImpl(
    private val datasetDao: DatasetDao
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
        datasetDao.deleteDatasetById(id)
    }

    override suspend fun getDatasetCount(): Int {
        return datasetDao.getDatasetCount()
    }
}
