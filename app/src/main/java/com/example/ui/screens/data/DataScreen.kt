package com.example.ui.screens.data

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.engine.ValidationResult
import com.example.domain.model.CsvColumnMapping
import com.example.domain.model.CsvInspectionResult
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.MappingValidationResult
import com.example.domain.model.ValidationStatus
import com.example.ui.components.ScientificPrincipleBanner
import com.example.ui.components.ValidationStatusBadge
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.PolishAmber
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishRose
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DataScreen(
    viewModel: DataViewModel = viewModel()
) {
    val datasets by viewModel.datasetsFlow.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.inspectCsvUri(it) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("data_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Data Management",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "OHLC INGESTION & SCIENTIFIC DATA INTEGRITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = Indigo600,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        item {
            ScientificPrincipleBanner(
                title = "GARBAGE IN, ILLUSION OUT",
                description = "Invalid data causes false edge discoveries. Every historical candle series must undergo chronological order checks, gap detection, and physical high/low boundary validation before research use."
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        csvPickerLauncher.launch(
                            arrayOf("text/*", "text/csv", "text/comma-separated-values", "application/csv", "*/*")
                        )
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("btn_import_csv"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Indigo600,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import CSV", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.loadDevelopmentSampleDataset() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_load_sample_data"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Slate900,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Load Sample", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.openImportDialog() },
                    modifier = Modifier
                        .weight(0.9f)
                        .height(44.dp)
                        .testTag("btn_import_csv_dialog"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate800),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Register", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }

        // CSV Inspection Loading State
        if (state.isInspectingCsv) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("csv_inspection_loading"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Indigo600.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Indigo600,
                            strokeWidth = 2.5.dp
                        )
                        Column {
                            Text(
                                text = "Inspecting Selected CSV...",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Reading file metadata, analyzing headers, and verifying schema",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // CSV Inspection Error (without result)
        if (state.csvInspectionError != null && state.csvInspectionResult == null) {
            item {
                CsvInspectionErrorCard(
                    errorMessage = state.csvInspectionError ?: "Unknown error",
                    onDismiss = { viewModel.clearCsvInspection() }
                )
            }
        }

        // CSV Inspection Result Card
        state.csvInspectionResult?.let { inspection ->
            item {
                CsvInspectionResultCard(
                    result = inspection,
                    onDismiss = { viewModel.clearCsvInspection() }
                )
            }

            // Phase 2 Milestone 2.2: CSV Column Mapping Card
            if (inspection.headers.isNotEmpty() && !inspection.isFileEmpty && inspection.errorMessage == null) {
                item {
                    CsvColumnMappingCard(
                        headers = inspection.headers,
                        mapping = state.csvColumnMapping,
                        validation = state.mappingValidationResult,
                        isConfirmed = state.isMappingConfirmed,
                        onMappingChanged = { field, selectedHeader ->
                            viewModel.updateColumnMapping(field, selectedHeader)
                        },
                        onResetToAuto = { viewModel.resetColumnMappingToAuto() },
                        onConfirmMapping = { viewModel.confirmColumnMapping() }
                    )
                }
            }
        }

        if (datasets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("empty_data_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Indigo50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Indigo600,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Datasets Registered",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Use 'Import CSV' to inspect your historical market data or load the development sample dataset.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(datasets, key = { it.datasetId }) { dataset ->
                DatasetItemCard(
                    dataset = dataset,
                    isSelected = state.selectedDataset?.datasetId == dataset.datasetId,
                    onClick = { viewModel.selectDataset(dataset) },
                    onDelete = { viewModel.deleteDataset(dataset.datasetId) },
                    onValidate = { viewModel.runValidationOnCurrentDataset() }
                )
            }
        }

        state.validationResult?.let { result ->
            item {
                ValidationReportCard(result = result)
            }
        }
    }

    if (state.showImportDialog) {
        CsvImportDialog(
            onDismiss = { viewModel.closeImportDialog() },
            onImport = { name, symbol, tf, source ->
                viewModel.importCsvMetadata(name, symbol, tf, source)
            }
        )
    }
}

@Composable
private fun DatasetItemCard(
    dataset: DatasetMetadata,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onValidate: () -> Unit
) {
    val borderColor = if (isSelected) Indigo600 else MaterialTheme.colorScheme.outline
    val bgColor = if (isSelected) Indigo50.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dataset_card_${dataset.datasetId}")
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dataset.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${dataset.symbol} · ${dataset.timeframe} · ${dataset.rowCount} Bars",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ValidationStatusBadge(status = dataset.validationStatus)
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("btn_delete_dataset_${dataset.datasetId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Dataset",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${dataset.source}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )

                Button(
                    onClick = onValidate,
                    modifier = Modifier.testTag("btn_validate_dataset_${dataset.datasetId}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Slate900,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Validate OHLC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ValidationReportCard(
    result: ValidationResult
) {
    val status = if (result.isValid) ValidationStatus.VALID else ValidationStatus.INVALID

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("validation_report_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DATASET AUDIT REPORT",
                    style = MaterialTheme.typography.labelSmall,
                    color = Indigo600,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
                ValidationStatusBadge(status = status)
            }

            Text(
                text = "${result.totalRows} Total Candles Audited",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AuditMetricBadge("Valid", "${result.validRows}", PolishEmerald, Modifier.weight(1f))
                AuditMetricBadge("Warnings", "${result.warnings.size}", if (result.warnings.isNotEmpty()) PolishAmber else Slate400, Modifier.weight(1f))
                AuditMetricBadge("Errors", "${result.errors.size}", if (result.errors.isNotEmpty()) PolishRose else Slate400, Modifier.weight(1f))
            }

            if (result.errors.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Slate100)
                )

                Text(
                    text = "Identified Issues:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold
                )

                result.errors.take(5).forEach { issue ->
                    Text(
                        text = "• ${issue.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PolishRose,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AuditMetricBadge(
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Slate100)
            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = tint,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CsvImportDialog(
    onDismiss: () -> Unit,
    onImport: (name: String, symbol: String, tf: String, source: String) -> Unit
) {
    var name by remember { mutableStateOf("EUR/USD Historical M15") }
    var symbol by remember { mutableStateOf("EUR/USD") }
    var timeframe by remember { mutableStateOf("M15") }
    var source by remember { mutableStateOf("Local CSV Import") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Register Dataset Metadata",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dataset Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_csv_name")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = symbol,
                        onValueChange = { symbol = it },
                        label = { Text("Symbol") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_csv_symbol")
                    )

                    OutlinedTextField(
                        value = timeframe,
                        onValueChange = { timeframe = it },
                        label = { Text("Timeframe") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_csv_timeframe")
                    )
                }

                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Source Provider") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_csv_source")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onImport(name, symbol, timeframe, source) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Indigo600,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_import")
            ) {
                Text("Register Dataset", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_import")
            ) {
                Text("Cancel", color = Slate600)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CsvInspectionResultCard(
    result: CsvInspectionResult,
    onDismiss: () -> Unit
) {
    val isSuccess = result.isHeaderValid && result.errorMessage == null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("csv_inspection_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSuccess) PolishEmerald.copy(alpha = 0.6f) else if (result.errorMessage != null || result.isFileEmpty) PolishRose.copy(alpha = 0.6f) else PolishAmber.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSuccess) Indigo50 else Slate100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = if (isSuccess) Indigo600 else Slate600,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "CSV FILE INSPECTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = Indigo600,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        )
                        Text(
                            text = result.fileName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("csv_file_name_text")
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_clear_inspection")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Inspection",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // File Size & Validation Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "File Size:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = result.formattedFileSize,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate800,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("csv_file_size_text")
                    )
                }

                // Status Pill
                val (statusText, statusBg, statusFg) = when {
                    result.isFileEmpty -> Triple("EMPTY CSV", PolishRose.copy(alpha = 0.12f), PolishRose)
                    result.errorMessage != null -> Triple("INSPECTION ERROR", PolishRose.copy(alpha = 0.12f), PolishRose)
                    result.isHeaderValid -> Triple("VALID OHLC HEADERS", PolishEmerald.copy(alpha = 0.12f), PolishEmerald)
                    else -> Triple("MISSING REQUIRED COLUMNS", PolishAmber.copy(alpha = 0.15f), PolishAmber)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusFg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Error display if any
            if (result.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PolishRose.copy(alpha = 0.1f))
                        .border(1.dp, PolishRose.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .testTag("csv_inspection_error")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = PolishRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = result.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = PolishRose,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Detected Headers Section
            if (result.headers.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Detected Column Headers (${result.headers.size}):",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate600,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("csv_headers_container"),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val standardOhlc = setOf("timestamp", "time", "date", "open", "high", "low", "close", "volume", "vol")
                        result.headers.forEach { header ->
                            val isStandard = standardOhlc.any { header.trim().lowercase().contains(it) }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isStandard) Indigo50 else Slate100)
                                    .border(
                                        1.dp,
                                        if (isStandard) Indigo600.copy(alpha = 0.4f) else Slate200,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = header,
                                    color = if (isStandard) Indigo600 else Slate800,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = if (isStandard) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Header Validation Issues
            if (result.headerErrors.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PolishAmber.copy(alpha = 0.08f))
                        .border(1.dp, PolishAmber.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Required Column Issues:",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishAmber,
                        fontWeight = FontWeight.Bold
                    )
                    result.headerErrors.forEach { issue ->
                        Text(
                            text = "• ${issue.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate800,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Sample Preview Rows
            if (result.samplePreviewRows.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sample Data Preview (${result.samplePreviewRows.size} row(s)):",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontWeight = FontWeight.Bold
                    )
                    result.samplePreviewRows.forEachIndexed { idx, row ->
                        Text(
                            text = "${idx + 1}: ${row.joinToString(" | ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CsvInspectionErrorCard(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("csv_inspection_error"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PolishRose.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PolishRose.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = PolishRose,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CSV Inspection Failed",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PolishRose
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700,
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Slate400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CsvColumnMappingCard(
    headers: List<String>,
    mapping: CsvColumnMapping,
    validation: MappingValidationResult,
    isConfirmed: Boolean,
    onMappingChanged: (String, String?) -> Unit,
    onResetToAuto: () -> Unit,
    onConfirmMapping: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("csv_mapping_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isConfirmed) PolishEmerald.copy(alpha = 0.6f)
            else if (validation.isValid) Indigo600.copy(alpha = 0.5f)
            else PolishAmber.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isConfirmed) PolishEmerald.copy(alpha = 0.12f) else Indigo50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.AltRoute,
                            contentDescription = null,
                            tint = if (isConfirmed) PolishEmerald else Indigo600,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "PHASE 2 • COLUMN MAPPING",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isConfirmed) PolishEmerald else Indigo600,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Map CSV Columns",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isConfirmed) PolishEmerald.copy(alpha = 0.12f)
                            else if (validation.isValid) Indigo50
                            else PolishAmber.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isConfirmed) "CONFIRMED" else if (validation.isValid) "READY TO CONFIRM" else "ACTION REQUIRED",
                        color = if (isConfirmed) PolishEmerald else if (validation.isValid) Indigo600 else PolishAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Text(
                text = "Assign detected CSV headers to Dr. Forex canonical fields. Real-world headers (e.g. Etc/UTC) are automatically recognized.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            // Mapping Dropdowns Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MappingDropdownField(
                    label = "Timestamp",
                    isRequired = true,
                    selectedValue = mapping.timestampColumn,
                    options = headers,
                    allowNotMapped = false,
                    testTag = "dropdown_mapping_timestamp",
                    onValueSelected = { onMappingChanged("timestamp", it) }
                )

                MappingDropdownField(
                    label = "Open",
                    isRequired = true,
                    selectedValue = mapping.openColumn,
                    options = headers,
                    allowNotMapped = false,
                    testTag = "dropdown_mapping_open",
                    onValueSelected = { onMappingChanged("open", it) }
                )

                MappingDropdownField(
                    label = "High",
                    isRequired = true,
                    selectedValue = mapping.highColumn,
                    options = headers,
                    allowNotMapped = false,
                    testTag = "dropdown_mapping_high",
                    onValueSelected = { onMappingChanged("high", it) }
                )

                MappingDropdownField(
                    label = "Low",
                    isRequired = true,
                    selectedValue = mapping.lowColumn,
                    options = headers,
                    allowNotMapped = false,
                    testTag = "dropdown_mapping_low",
                    onValueSelected = { onMappingChanged("low", it) }
                )

                MappingDropdownField(
                    label = "Close",
                    isRequired = true,
                    selectedValue = mapping.closeColumn,
                    options = headers,
                    allowNotMapped = false,
                    testTag = "dropdown_mapping_close",
                    onValueSelected = { onMappingChanged("close", it) }
                )

                MappingDropdownField(
                    label = "Volume",
                    isRequired = false,
                    selectedValue = mapping.volumeColumn,
                    options = headers,
                    allowNotMapped = true,
                    testTag = "dropdown_mapping_volume",
                    onValueSelected = { onMappingChanged("volume", it) }
                )
            }

            // Validation / Feedback Box
            if (isConfirmed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PolishEmerald.copy(alpha = 0.1f))
                        .border(1.dp, PolishEmerald.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .testTag("mapping_confirmed_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PolishEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Column Mapping Confirmed",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishEmerald
                            )
                            Text(
                                text = "All required fields are mapped and verified. Ready for market data ingestion.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate800,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else if (!validation.isValid && validation.errorMessages.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PolishRose.copy(alpha = 0.08f))
                        .border(1.dp, PolishRose.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .testTag("mapping_errors_box"),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = PolishRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (validation.missingRequiredFields.isNotEmpty()) "Required field not mapped:" else "Mapping validation error:",
                            style = MaterialTheme.typography.labelSmall,
                            color = PolishRose,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    validation.errorMessages.forEach { errorMsg ->
                        Text(
                            text = "• $errorMsg",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate800,
                            fontSize = 11.sp
                        )
                    }
                }
            } else if (validation.isValid) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PolishEmerald.copy(alpha = 0.08f))
                        .border(1.dp, PolishEmerald.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                        .testTag("mapping_valid_indicator")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = PolishEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "All required OHLC columns mapped. Click 'Confirm Mapping' to proceed.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PolishEmerald,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onResetToAuto,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_reset_mapping"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Slate700
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset to Auto", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Slate700)
                }

                Button(
                    onClick = onConfirmMapping,
                    enabled = validation.isValid,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("btn_confirm_mapping"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConfirmed) PolishEmerald else Indigo600,
                        contentColor = Color.White,
                        disabledContainerColor = Slate200,
                        disabledContentColor = Slate400
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (isConfirmed) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isConfirmed) "Mapping Confirmed" else "Confirm Mapping",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MappingDropdownField(
    label: String,
    isRequired: Boolean,
    selectedValue: String?,
    options: List<String>,
    allowNotMapped: Boolean,
    testTag: String,
    onValueSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
            )
            if (isRequired) {
                Text(
                    text = "*",
                    color = PolishRose,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            } else {
                Text(
                    text = "(optional)",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedCard(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (selectedValue != null) Indigo50.copy(alpha = 0.5f) else Slate50
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedValue != null) Indigo600.copy(alpha = 0.5f) else Slate200
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedValue ?: "(Not mapped)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedValue != null) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedValue != null) Slate900 else Slate400,
                        fontFamily = if (selectedValue != null) FontFamily.Monospace else FontFamily.Default,
                        fontSize = 13.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select $label column",
                        tint = if (selectedValue != null) Indigo600 else Slate400
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                if (allowNotMapped) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "(Not mapped)",
                                color = Slate400,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        },
                        onClick = {
                            onValueSelected(null)
                            expanded = false
                        }
                    )
                }
                options.forEach { header ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = header,
                                fontWeight = if (header == selectedValue) FontWeight.Bold else FontWeight.Normal,
                                color = if (header == selectedValue) Indigo600 else Slate800,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        onClick = {
                            onValueSelected(header)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
