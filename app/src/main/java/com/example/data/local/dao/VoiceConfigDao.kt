package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.VoiceConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceConfigDao {

    @Query("SELECT * FROM voice_config WHERE id = 1 LIMIT 1")
    fun getVoiceConfigFlow(): Flow<VoiceConfigEntity?>

    @Query("SELECT * FROM voice_config WHERE id = 1 LIMIT 1")
    suspend fun getVoiceConfig(): VoiceConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: VoiceConfigEntity)
}
