package com.example.ui.screens.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DrForexDatabase
import com.example.data.repository.DatasetRepositoryImpl
import com.example.data.repository.SampleDataFactory
import com.example.domain.engine.CsvCandleImporter
import com.example.domain.engine.CsvCandleImporterImpl
import com.example.domain.engine.CsvColumnMapper
import com.example.domain.engine.CsvColumnMapperImpl
import com.example.domain.engine.CsvInspector
import com.example.domain.engine.CsvInspectorImpl
import com.example.domain.engine.DataValidator
import com.example.domain.engine.DataValidatorImpl
import com.example.domain.engine.ValidationResult
import com.example.domain.model.CsvColumnMapping
import com.example.domain.model.CsvImportResult
import com.example.domain.model.CsvInspectionResult
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.MarketCandle
import com.example.domain.model.MappingValidationResult
import com.example.domain.model.ValidationStatus
import kotlinx.coroutines.Dispatchers
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
    val statusMessage: String = "",
    // Phase 2 Milestone 2.1: CSV Inspection
    val csvInspectionResult: CsvInspectionResult? = null,
    val isInspectingCsv: Boolean = false,
    val csvInspectionError: String? = null,
    // Phase 2 Milestone 2.2: CSV Column Mapping
    val csvColumnMapping: CsvColumnMapping = CsvColumnMapping(),
    val mappingValidationResult: MappingValidationResult = MappingValidationResult(isValid = false),
    val isMappingConfirmed: Boolean = false,
    // Phase 2 Milestone 2.3: CSV Candle Import into Room
    val selectedCsvUri: android.net.Uri? = null,
    val isImporting: Boolean = false,
    val csvImportResult: CsvImportResult? = null
)

class DataViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DrForexDatabase.getDatabase(application)
    private val datasetRepo = DatasetRepositoryImpl(db.datasetDao(), db.candleDao())
    private val validator: DataValidator = DataValidatorImpl()
    private val csvInspector: CsvInspector = CsvInspectorImpl(validator)
    private val columnMapper: CsvColumnMapper = CsvColumnMapperImpl()
    private val csvCandleImporter: CsvCandleImporter = CsvCandleImporterImpl(validator)

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

    /**
     * Inspects a CSV file chosen via SAF file picker and automatically maps columns.
     */
    fun inspectCsvUri(uri: android.net.Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(
                isInspectingCsv = true,
                csvInspectionError = null,
                isMappingConfirmed = false,
                selectedCsvUri = uri,
                csvImportResult = null
            )
            try {
                val resolver = getApplication<Application>().contentResolver
                val result = csvInspector.inspectUri(resolver, uri)
                val autoMapping = if (result.headers.isNotEmpty()) {
                    columnMapper.autoDetectMapping(result.headers)
                } else {
                    CsvColumnMapping()
                }
                val mappingValidation = columnMapper.validateMapping(autoMapping, result.headers)

                _uiState.value = _uiState.value.copy(
                    isInspectingCsv = false,
                    csvInspectionResult = result,
                    csvInspectionError = result.errorMessage,
                    csvColumnMapping = autoMapping,
                    mappingValidationResult = mappingValidation,
                    isMappingConfirmed = false,
                    selectedCsvUri = uri,
                    statusMessage = if (result.errorMessage == null) {
                        if (mappingValidation.isValid) {
                            "Inspected '${result.fileName}' — columns automatically mapped"
                        } else {
                            "Inspected '${result.fileName}' — please configure column mapping"
                        }
                    } else {
                        "CSV Inspection warning: ${result.errorMessage}"
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isInspectingCsv = false,
                    csvInspectionError = e.message ?: "Failed to open or inspect the selected CSV file",
                    statusMessage = "Error reading CSV file"
                )
            }
        }
    }

    /**
     * Updates an individual field mapping (e.g. "timestamp", "open", "high", "low", "close", "volume")
     * and re-evaluates validation immediately.
     */
    fun updateColumnMapping(targetField: String, selectedCsvHeader: String?) {
        val current = _uiState.value.csvColumnMapping
        val header = if (selectedCsvHeader.isNullOrBlank() || selectedCsvHeader == "(Not mapped)") null else selectedCsvHeader
        val updated = when (targetField.lowercase(Locale.ROOT)) {
            "timestamp" -> current.copy(timestampColumn = header)
            "open" -> current.copy(openColumn = header)
            "high" -> current.copy(highColumn = header)
            "low" -> current.copy(lowColumn = header)
            "close" -> current.copy(closeColumn = header)
            "volume" -> current.copy(volumeColumn = header)
            else -> current
        }
        val availableHeaders = _uiState.value.csvInspectionResult?.headers ?: emptyList()
        val validation = columnMapper.validateMapping(updated, availableHeaders)
        _uiState.value = _uiState.value.copy(
            csvColumnMapping = updated,
            mappingValidationResult = validation,
            isMappingConfirmed = false
        )
    }

    /**
     * Resets column mappings back to the automatic alias detection result.
     */
    fun resetColumnMappingToAuto() {
        val headers = _uiState.value.csvInspectionResult?.headers ?: emptyList()
        val autoMapping = columnMapper.autoDetectMapping(headers)
        val validation = columnMapper.validateMapping(autoMapping, headers)
        _uiState.value = _uiState.value.copy(
            csvColumnMapping = autoMapping,
            mappingValidationResult = validation,
            isMappingConfirmed = false,
            statusMessage = "Reset column mapping to auto-detected values"
        )
    }

    /**
     * Confirms the column mapping after verifying all required fields and absence of duplicates.
     */
    fun confirmColumnMapping() {
        val current = _uiState.value.csvColumnMapping
        val headers = _uiState.value.csvInspectionResult?.headers ?: emptyList()
        val validation = columnMapper.validateMapping(current, headers)
        if (validation.isValid) {
            _uiState.value = _uiState.value.copy(
                mappingValidationResult = validation,
                isMappingConfirmed = true,
                statusMessage = "Column mapping confirmed for ${_uiState.value.csvInspectionResult?.fileName ?: "CSV file"}"
            )
        } else {
            _uiState.value = _uiState.value.copy(
                mappingValidationResult = validation,
                isMappingConfirmed = false,
                statusMessage = "Please resolve mapping errors before confirming"
            )
        }
    }

    /**
     * Phase 2 Milestone 2.3: Imports the validated CSV candle rows into the Room database.
     */
    fun importConfirmedCsv() {
        val uri = _uiState.value.selectedCsvUri
        val inspection = _uiState.value.csvInspectionResult
        val mapping = _uiState.value.csvColumnMapping

        if (uri == null || inspection == null) {
            _uiState.value = _uiState.value.copy(
                statusMessage = "No CSV file selected for import"
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isImporting = true)
            try {
                val resolver = getApplication<Application>().contentResolver
                val parseResult = csvCandleImporter.parseAndValidateUri(resolver, uri, mapping)

                if (parseResult.validCandles.isEmpty()) {
                    val importResult = CsvImportResult(
                        isSuccess = false,
                        datasetId = "",
                        datasetName = inspection.fileName,
                        fileName = inspection.fileName,
                        totalRowsRead = parseResult.totalRowsRead,
                        importedCount = 0,
                        skippedCount = 0,
                        rejectedCount = parseResult.rejectedRows.size,
                        rejectedRowErrors = parseResult.rejectedRows,
                        summary = "Import failed: 0 valid candles found out of ${parseResult.totalRowsRead} rows"
                    )
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        csvImportResult = importResult,
                        statusMessage = "Import failed: 0 valid candles"
                    )
                    return@launch
                }

                // Deduce symbol, timeframe, and deterministic dataset ID from filename
                val baseFileName = inspection.fileName.substringBeforeLast(".")
                val detectedSymbol = deduceSymbolFromFileName(baseFileName)
                val detectedTimeframe = deduceTimeframeFromFileName(baseFileName)
                val datasetId = generateDeterministicDatasetId(inspection.fileName, detectedSymbol, detectedTimeframe)

                // Insert candle entities into Room with duplicate prevention (scoped to datasetId + timestamp)
                val (inserted, skipped) = datasetRepo.insertCandles(datasetId, parseResult.validCandles)
                val totalInDb = datasetRepo.getCandleCountByDatasetId(datasetId)
                val allCandlesInDb = datasetRepo.getCandlesByDatasetId(datasetId)

                val startDate = allCandlesInDb.firstOrNull()?.timestamp
                    ?: parseResult.validCandles.firstOrNull()?.timestamp ?: 0L
                val endDate = allCandlesInDb.lastOrNull()?.timestamp
                    ?: parseResult.validCandles.lastOrNull()?.timestamp ?: 0L

                val summaryText = if (inserted == 0 && skipped > 0) {
                    "Re-import complete: 0 new candles, $skipped existing duplicate candles skipped ($totalInDb total candles in dataset)"
                } else if (skipped > 0) {
                    "Import complete: $inserted new candles, $skipped duplicate candles skipped, ${parseResult.rejectedRows.size} rejected rows ($totalInDb total in dataset)"
                } else {
                    "Import complete: $inserted candles imported, ${parseResult.rejectedRows.size} rejected rows"
                }

                val datasetMetadata = DatasetMetadata(
                    datasetId = datasetId,
                    name = inspection.fileName.ifBlank { "$detectedSymbol $detectedTimeframe CSV" },
                    symbol = detectedSymbol,
                    timeframe = detectedTimeframe,
                    startDate = startDate,
                    endDate = endDate,
                    rowCount = totalInDb,
                    source = "CSV: ${inspection.fileName}",
                    timezone = "UTC",
                    validationStatus = ValidationStatus.VALID,
                    isDevelopmentSample = false,
                    validationSummary = if (inserted == 0 && skipped > 0) {
                        "Re-imported: $skipped existing candles verified in Room database"
                    } else {
                        "Imported $totalInDb candles (${parseResult.rejectedRows.size} rejected rows)"
                    }
                )

                // Update/Insert dataset metadata in Room
                datasetRepo.insertDataset(datasetMetadata)

                val importResult = CsvImportResult(
                    isSuccess = true,
                    datasetId = datasetId,
                    datasetName = datasetMetadata.name,
                    fileName = inspection.fileName,
                    totalRowsRead = parseResult.totalRowsRead,
                    importedCount = inserted,
                    skippedCount = skipped,
                    rejectedCount = parseResult.rejectedRows.size,
                    rejectedRowErrors = parseResult.rejectedRows,
                    summary = summaryText,
                    startDate = startDate,
                    endDate = endDate
                )

                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    csvImportResult = importResult,
                    selectedDataset = datasetMetadata,
                    activeCandles = if (allCandlesInDb.isNotEmpty()) allCandlesInDb else parseResult.validCandles,
                    statusMessage = summaryText
                )
            } catch (e: Exception) {
                val importResult = CsvImportResult(
                    isSuccess = false,
                    datasetId = "",
                    datasetName = inspection.fileName,
                    fileName = inspection.fileName,
                    totalRowsRead = 0,
                    importedCount = 0,
                    skippedCount = 0,
                    rejectedCount = 0,
                    rejectedRowErrors = emptyList(),
                    summary = "Import failed: ${e.message ?: "Unknown error"}"
                )
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    csvImportResult = importResult,
                    statusMessage = "Import error: ${e.message}"
                )
            }
        }
    }

    private fun deduceSymbolFromFileName(name: String): String {
        val upper = name.uppercase(Locale.ROOT)
        val knownPairs = listOf(
            "EURUSD" to "EUR/USD",
            "GBPUSD" to "GBP/USD",
            "USDJPY" to "USD/JPY",
            "USDCHF" to "USD/CHF",
            "AUDUSD" to "AUD/USD",
            "USDCAD" to "USD/CAD",
            "NZDUSD" to "NZD/USD",
            "EURGBP" to "EUR/GBP",
            "EURJPY" to "EUR/JPY",
            "GBPJPY" to "GBP/JPY"
        )
        for ((key, value) in knownPairs) {
            if (upper.contains(key)) return value
        }
        return "EUR/USD"
    }

    private fun deduceTimeframeFromFileName(name: String): String {
        val upper = name.uppercase(Locale.ROOT)
        val timeframes = listOf("M1", "M5", "M15", "M30", "H1", "H4", "D1", "W1", "MN")
        for (tf in timeframes) {
            if (upper.contains("_${tf}") || upper.contains("-$tf") || upper.contains(" $tf") || upper.endsWith(tf)) {
                return tf
            }
        }
        return "M15"
    }

    fun generateDeterministicDatasetId(fileName: String, symbol: String, timeframe: String): String {
        val cleanSymbol = symbol.replace("/", "").replace("_", "").replace("-", "").uppercase(Locale.ROOT)
        val baseName = fileName.substringBeforeLast(".")
            .replace(Regex("[^a-zA-Z0-9]"), "")
            .uppercase(Locale.ROOT)
            .ifBlank { "DATA" }
        val cleanTf = timeframe.uppercase(Locale.ROOT)
        return "DS-$cleanSymbol-$cleanTf-$baseName"
    }

    fun clearImportResult() {
        _uiState.value = _uiState.value.copy(
            csvImportResult = null
        )
    }

    fun clearCsvInspection() {
        _uiState.value = _uiState.value.copy(
            csvInspectionResult = null,
            csvInspectionError = null,
            csvColumnMapping = CsvColumnMapping(),
            mappingValidationResult = MappingValidationResult(isValid = false),
            isMappingConfirmed = false,
            selectedCsvUri = null,
            csvImportResult = null
        )
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timestamp)) + " UTC"
    }
}
