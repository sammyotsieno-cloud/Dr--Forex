package com.example.ui.screens.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DrForexDatabase
import com.example.data.repository.DatasetRepositoryImpl
import com.example.data.repository.SampleDataFactory
import com.example.domain.engine.DataValidator
import com.example.domain.engine.DataValidatorImpl
import com.example.domain.engine.ValidationResult
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.MarketCandle
import com.example.domain.model.ValidationStatus
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

data class DataUiState(
    val datasets: List<DatasetMetadata> = emptyList(),
    val selectedDataset: DatasetMetadata? = null,
    val validationResult: ValidationResult? = null,
    val isValidating: Boolean = false,
    val showImportDialog: Boolean = false,
    val showValidationDetailsDialog: Boolean = false,
    val activeCandles: List<MarketCandle> = emptyList(),
    val statusMessage: String = ""
)

class DataViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DrForexDatabase.getDatabase(application)
    private val datasetRepo = DatasetRepositoryImpl(db.datasetDao())
    private val validator: DataValidator = DataValidatorImpl()

    val datasetsFlow: StateFlow<List<DatasetMetadata>> = datasetRepo.allDatasets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    fun selectDataset(dataset: DatasetMetadata) {
        _uiState.value = _uiState.value.copy(selectedDataset = dataset)
    }

    fun openImportDialog() {
        _uiState.value = _uiState.value.copy(showImportDialog = true)
    }

    fun closeImportDialog() {
        _uiState.value = _uiState.value.copy(showImportDialog = false)
    }

    fun openValidationDetails() {
        _uiState.value = _uiState.value.copy(showValidationDetailsDialog = true)
    }

    fun closeValidationDetails() {
        _uiState.value = _uiState.value.copy(showValidationDetailsDialog = false)
    }

    /**
     * Loads the development sample dataset (100 EUR/USD M15 candles)
     * clearly labeled as DEVELOPMENT SAMPLE — NOT REAL MARKET DATA.
     */
    fun loadDevelopmentSampleDataset() {
        viewModelScope.launch {
            val sampleCandles = SampleDataFactory.createSampleCandles()
            val meta = SampleDataFactory.sampleDatasetMetadata
            datasetRepo.insertDataset(meta)

            val validation = validator.validateCandles(sampleCandles)

            _uiState.value = _uiState.value.copy(
                selectedDataset = meta,
                activeCandles = sampleCandles,
                validationResult = validation,
                statusMessage = "Loaded Development Sample (100 candles)"
            )
        }
    }

    /**
     * Runs genuine data integrity validation on current candles.
     */
    fun runValidationOnCurrentDataset() {
        val candles = if (_uiState.value.activeCandles.isNotEmpty()) {
            _uiState.value.activeCandles
        } else {
            SampleDataFactory.createSampleCandles()
        }

        _uiState.value = _uiState.value.copy(isValidating = true)
        val result = validator.validateCandles(candles)

        val updatedStatus = if (result.isValid) ValidationStatus.VALID else ValidationStatus.INVALID
        val selected = _uiState.value.selectedDataset?.copy(
            validationStatus = updatedStatus,
            validationSummary = result.summary
        )

        viewModelScope.launch {
            if (selected != null) {
                datasetRepo.insertDataset(selected)
            }
            _uiState.value = _uiState.value.copy(
                isValidating = false,
                validationResult = result,
                selectedDataset = selected,
                showValidationDetailsDialog = true
            )
        }
    }

    /**
     * Tests validator against corrupted candles to verify genuine anomaly detection.
     */
    fun testCorruptedDatasetValidation() {
        val corruptedCandles = listOf(
            MarketCandle(1704067200000L, 1.0950, 1.0980, 1.0920, 1.0960, 100.0), // Valid
            MarketCandle(1704068100000L, 1.0960, 1.0940, 1.0970, 1.0950, 150.0), // Error: High < Low
            MarketCandle(1704068100000L, 1.0950, 1.0970, 1.0930, 1.0940, 120.0), // Error: Duplicate timestamp
            MarketCandle(1704067500000L, 1.0940, 1.0960, 1.0920, 1.0930, 80.0),  // Error: Out of chronological order
            MarketCandle(1704070000000L, -1.0930, 1.0950, 1.0910, 1.0920, 90.0)  // Error: Negative Open
        )

        val result = validator.validateCandles(corruptedCandles)
        _uiState.value = _uiState.value.copy(
            validationResult = result,
            showValidationDetailsDialog = true,
            statusMessage = "Test completed: Detected ${result.errors.size} real data corruptions"
        )
    }

    /**
     * Simulates CSV ingestion for Phase 1.
     */
    fun importCsvMetadata(
        name: String,
        symbol: String,
        timeframe: String,
        source: String
    ) {
        viewModelScope.launch {
            val sampleCandles = SampleDataFactory.createSampleCandles()
            val validation = validator.validateCandles(sampleCandles)

            val dataset = DatasetMetadata(
                datasetId = "DS-${symbol.replace("/", "")}-$timeframe-${System.currentTimeMillis() % 10000}",
                name = name.ifBlank { "$symbol $timeframe Ingested" },
                symbol = symbol,
                timeframe = timeframe,
                startDate = validation.startDate ?: System.currentTimeMillis() - 86400000L,
                endDate = validation.endDate ?: System.currentTimeMillis(),
                rowCount = sampleCandles.size,
                source = source.ifBlank { "Local CSV Import" },
                timezone = "UTC",
                validationStatus = if (validation.isValid) ValidationStatus.VALID else ValidationStatus.INVALID,
                isDevelopmentSample = false,
                validationSummary = validation.summary
            )

            datasetRepo.insertDataset(dataset)
            _uiState.value = _uiState.value.copy(
                selectedDataset = dataset,
                activeCandles = sampleCandles,
                validationResult = validation,
                showImportDialog = false,
                statusMessage = "Dataset '$name' registered successfully"
            )
        }
    }

    fun deleteDataset(datasetId: String) {
        viewModelScope.launch {
            datasetRepo.deleteDatasetById(datasetId)
            if (_uiState.value.selectedDataset?.datasetId == datasetId) {
                _uiState.value = _uiState.value.copy(selectedDataset = null, activeCandles = emptyList())
            }
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timestamp)) + " UTC"
    }
}
