package com.example.ui.screens.experiments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DrForexDatabase
import com.example.data.repository.DatasetRepositoryImpl
import com.example.data.repository.ExperimentRepositoryImpl
import com.example.data.repository.ResearchConfigRepositoryImpl
import com.example.domain.experiment.ExperimentManager
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.Experiment
import com.example.domain.model.ExperimentStatus
import com.example.domain.model.PerformanceMetrics
import com.example.domain.model.ResearchConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class ExperimentsUiState(
    val selectedExperiment: Experiment? = null,
    val showCreateDialog: Boolean = false,
    val showDetailDialog: Boolean = false,
    val nextExperimentId: String = "EXP-0001",
    val config: ResearchConfiguration = ResearchConfiguration(),
    val datasets: List<DatasetMetadata> = emptyList(),
    val statusMessage: String = ""
)

class ExperimentsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DrForexDatabase.getDatabase(application)
    private val experimentRepo = ExperimentRepositoryImpl(db.experimentDao())
    private val datasetRepo = DatasetRepositoryImpl(db.datasetDao())
    private val configRepo = ResearchConfigRepositoryImpl(db.researchConfigDao())
    private val experimentManager = ExperimentManager(experimentRepo)

    val experimentsFlow: StateFlow<List<Experiment>> = experimentManager.allExperiments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ExperimentsUiState())
    val uiState: StateFlow<ExperimentsUiState> = _uiState.asStateFlow()

    init {
        loadInitialContext()
    }

    private fun loadInitialContext() {
        viewModelScope.launch {
            val nextId = experimentManager.generateNextExperimentId()
            val config = configRepo.getConfig()
            _uiState.value = _uiState.value.copy(
                nextExperimentId = nextId,
                config = config
            )
        }
        viewModelScope.launch {
            datasetRepo.allDatasets.collect { list ->
                _uiState.value = _uiState.value.copy(datasets = list)
            }
        }
    }

    fun openCreateDialog() {
        viewModelScope.launch {
            val nextId = experimentManager.generateNextExperimentId()
            _uiState.value = _uiState.value.copy(
                showCreateDialog = true,
                nextExperimentId = nextId
            )
        }
    }

    fun closeCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun selectExperiment(experiment: Experiment) {
        _uiState.value = _uiState.value.copy(
            selectedExperiment = experiment,
            showDetailDialog = true
        )
    }

    fun closeDetailDialog() {
        _uiState.value = _uiState.value.copy(showDetailDialog = false)
    }

    /**
     * Creates and records a new empirical experiment into Room.
     */
    fun createExperiment(
        strategyName: String,
        instrument: String,
        timeframe: String,
        parameters: String,
        notes: String
    ) {
        viewModelScope.launch {
            val config = configRepo.getConfig()
            val activeDataset = _uiState.value.datasets.firstOrNull()

            val exp = experimentManager.createExperiment(
                strategyId = "STRAT-${strategyName.take(6).uppercase()}",
                strategyName = strategyName.ifBlank { "Quantitative Hypothesis" },
                datasetId = activeDataset?.datasetId ?: "DS-EURUSD-M15",
                datasetName = activeDataset?.name ?: "EUR/USD Historical M15",
                instrument = instrument.ifBlank { config.defaultInstrument },
                timeframe = timeframe.ifBlank { config.defaultExecutionTimeframe },
                parametersSummary = parameters.ifBlank { "Default Parameters" },
                notes = notes.ifBlank { "Hypothesis recorded for Phase 5 Backtester" },
                config = config,
                status = ExperimentStatus.CONFIGURED,
                metrics = null // Phase 1 does not simulate fake metrics
            )

            val nextId = experimentManager.generateNextExperimentId()
            _uiState.value = _uiState.value.copy(
                showCreateDialog = false,
                nextExperimentId = nextId,
                statusMessage = "Created Experiment ${exp.experimentId}"
            )
        }
    }

    fun deleteExperiment(id: String) {
        viewModelScope.launch {
            experimentManager.deleteExperiment(id)
            val nextId = experimentManager.generateNextExperimentId()
            _uiState.value = _uiState.value.copy(
                nextExperimentId = nextId,
                selectedExperiment = null,
                showDetailDialog = false
            )
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timestamp)) + " UTC"
    }
}
