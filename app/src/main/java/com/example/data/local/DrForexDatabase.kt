package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.DatasetDao
import com.example.data.local.dao.ExperimentDao
import com.example.data.local.dao.ResearchConfigDao
import com.example.data.local.entity.DatasetEntity
import com.example.data.local.entity.ExperimentEntity
import com.example.data.local.entity.ResearchConfigEntity

@Database(
    entities = [
        ExperimentEntity::class,
        DatasetEntity::class,
        ResearchConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DrForexDatabase : RoomDatabase() {

    abstract fun experimentDao(): ExperimentDao
    abstract fun datasetDao(): DatasetDao
    abstract fun researchConfigDao(): ResearchConfigDao

    companion object {
        @Volatile
        private var INSTANCE: DrForexDatabase? = null

        fun getDatabase(context: Context): DrForexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrForexDatabase::class.java,
                    "dr_forex_research_lab.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
