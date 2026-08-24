package com.example.data.repository

import com.example.data.local.dao.ExperimentDao
import com.example.data.local.entity.ExperimentEntity
import com.example.domain.model.Experiment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ExperimentRepository {
    val allExperiments: Flow<List<Experiment>>
    suspend fun getExperimentById(id: String): Experiment?
    suspend fun insertExperiment(experiment: Experiment)
    suspend fun deleteExperimentById(id: String)
    suspend fun getExperimentCount(): Int
    suspend fun getLatestExperimentId(): String?
}

class ExperimentRepositoryImpl(
    private val experimentDao: ExperimentDao
) : ExperimentRepository {

    override val allExperiments: Flow<List<Experiment>> =
        experimentDao.getAllExperiments().map { list -> list.map { it.toDomain() } }

    override suspend fun getExperimentById(id: String): Experiment? {
        return experimentDao.getExperimentById(id)?.toDomain()
    }

    override suspend fun insertExperiment(experiment: Experiment) {
        experimentDao.insertExperiment(ExperimentEntity.fromDomain(experiment))
    }

    override suspend fun deleteExperimentById(id: String) {
        experimentDao.deleteExperimentById(id)
    }

    override suspend fun getExperimentCount(): Int {
        return experimentDao.getExperimentCount()
    }

    override suspend fun getLatestExperimentId(): String? {
        return experimentDao.getLatestExperimentId()
    }
}
