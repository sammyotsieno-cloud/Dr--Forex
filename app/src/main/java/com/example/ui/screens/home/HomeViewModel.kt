package com.example.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DrForexDatabase
import com.example.data.repository.DatasetRepositoryImpl
import com.example.data.repository.ExperimentRepositoryImpl
import com.example.data.repository.ResearchConfigRepositoryImpl
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.ResearchConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val activeDataset: DatasetMetadata? = null,
    val datasetCount: Int = 0,
    val experimentCount: Int = 0,
    val researchEngineStatus: String = "Ready",
    val systemStatus: String = "Operational",
    val configuration: ResearchConfiguration = ResearchConfiguration(),
    val isReady: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DrForexDatabase.getDatabase(application)
    private val experimentRepo = ExperimentRepositoryImpl(db.experimentDao())
    private val datasetRepo = DatasetRepositoryImpl(db.datasetDao())
    private val configRepo = ResearchConfigRepositoryImpl(db.researchConfigDao())

    val uiState: StateFlow<HomeUiState> = combine(
        experimentRepo.allExperiments,
        datasetRepo.allDatasets,
        configRepo.configFlow
    ) { experiments, datasets, config ->
        HomeUiState(
            activeDataset = datasets.firstOrNull(),
            datasetCount = datasets.size,
            experimentCount = experiments.size,
            researchEngineStatus = "Ready (Phase 1)",
            systemStatus = "Operational",
            configuration = config,
            isReady = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
