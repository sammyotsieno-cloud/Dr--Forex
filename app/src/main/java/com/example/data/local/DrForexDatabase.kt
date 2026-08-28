package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CandleDao
import com.example.data.local.dao.DatasetDao
import com.example.data.local.dao.ExperimentDao
import com.example.data.local.dao.KnowledgeDao
import com.example.data.local.dao.ResearchConfigDao
import com.example.data.local.dao.VoiceConfigDao
import com.example.data.local.entity.CandleEntity
import com.example.data.local.entity.DatasetEntity
import com.example.data.local.entity.ExperimentEntity
import com.example.data.local.entity.LearnedPatternEntity
import com.example.data.local.entity.ResearchConfigEntity
import com.example.data.local.entity.TradeObservationEntity
import com.example.data.local.entity.VoiceConfigEntity

@Database(
    entities = [
        ExperimentEntity::class,
        DatasetEntity::class,
        ResearchConfigEntity::class,
        CandleEntity::class,
        VoiceConfigEntity::class,
        TradeObservationEntity::class,
        LearnedPatternEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DrForexDatabase : RoomDatabase() {

    abstract fun experimentDao(): ExperimentDao
    abstract fun datasetDao(): DatasetDao
    abstract fun researchConfigDao(): ResearchConfigDao
    abstract fun candleDao(): CandleDao
    abstract fun voiceConfigDao(): VoiceConfigDao
    abstract fun knowledgeDao(): KnowledgeDao

    companion object {
        @Volatile
        private var INSTANCE: DrForexDatabase? = null

        fun getDatabase(context: Context): DrForexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrForexDatabase::class.java,
                    "dr_forex_research_lab.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

