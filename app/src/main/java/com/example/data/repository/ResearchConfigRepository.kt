package com.example.data.repository

import com.example.data.local.dao.ResearchConfigDao
import com.example.data.local.entity.ResearchConfigEntity
import com.example.domain.model.ResearchConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ResearchConfigRepository {
    val configFlow: Flow<ResearchConfiguration>
    suspend fun getConfig(): ResearchConfiguration
    suspend fun saveConfig(config: ResearchConfiguration)
}

class ResearchConfigRepositoryImpl(
    private val researchConfigDao: ResearchConfigDao
) : ResearchConfigRepository {

    private val defaultConfig = ResearchConfiguration()

    override val configFlow: Flow<ResearchConfiguration> =
        researchConfigDao.getConfigFlow().map { entity ->
            entity?.toDomain() ?: defaultConfig
        }

    override suspend fun getConfig(): ResearchConfiguration {
        return researchConfigDao.getConfig()?.toDomain() ?: defaultConfig
    }

    override suspend fun saveConfig(config: ResearchConfiguration) {
        researchConfigDao.saveConfig(ResearchConfigEntity.fromDomain(config))
    }
}
