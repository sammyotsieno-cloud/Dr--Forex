package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ResearchConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchConfigDao {
    @Query("SELECT * FROM research_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<ResearchConfigEntity?>

    @Query("SELECT * FROM research_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): ResearchConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: ResearchConfigEntity)
}
