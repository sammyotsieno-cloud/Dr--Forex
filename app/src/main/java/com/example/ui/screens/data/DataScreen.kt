package com.example.ui.screens.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.domain.model.DatasetMetadata
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
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun DataScreen(
    viewModel: DataViewModel = viewModel()
) {
    val datasets by viewModel.datasetsFlow.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.loadDevelopmentSampleDataset() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_load_sample_data"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Indigo600,
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
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_import_csv_dialog"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate800),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register CSV", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
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
                            text = "Load the development sample dataset (EUR/USD M15) or register metadata to begin quantitative research.",
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
