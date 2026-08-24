package com.example.ui.screens.experiments

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.domain.model.Experiment
import com.example.domain.model.ExperimentStatus
import com.example.ui.components.ScientificPrincipleBanner
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Indigo700
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
fun ExperimentsScreen(
    viewModel: ExperimentsViewModel = viewModel()
) {
    val experiments by viewModel.experimentsFlow.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("experiments_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Experiment Registry",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "IMMUTABLE AUDIT TRAIL FOR HYPOTHESIS TESTING",
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
                title = "SCIENTIFIC AUDIT & REPRODUCIBILITY",
                description = "Every experiment receives a unique sequential ID (e.g. EXP-0001) and permanently records all parameters, rules, datasets, and rejection notes. Cherry-picking or deleting failed experiments is forbidden."
            )
        }

        item {
            Button(
                onClick = { viewModel.openCreateDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_open_create_experiment"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Slate900,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REGISTER NEW RESEARCH EXPERIMENT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (experiments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("empty_experiments_card"),
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
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Indigo600,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Experiments Registered",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Register your first experiment to begin formal quantitative hypothesis logging.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(experiments, key = { it.experimentId }) { experiment ->
                ExperimentItemCard(
                    experiment = experiment,
                    isSelected = state.selectedExperiment?.experimentId == experiment.experimentId,
                    onClick = { viewModel.selectExperiment(experiment) },
                    onDelete = { viewModel.deleteExperiment(experiment.experimentId) }
                )
            }
        }

        state.selectedExperiment?.let { selected ->
            item {
                ExperimentAuditDetailCard(
                    experiment = selected,
                    formattedDate = viewModel.formatDate(selected.timestamp)
                )
            }
        }
    }

    if (state.showCreateDialog) {
        CreateExperimentDialog(
            nextId = state.nextExperimentId,
            onDismiss = { viewModel.closeCreateDialog() },
            onCreate = { stratName, instrument, timeframe, params, notes ->
                viewModel.createExperiment(stratName, instrument, timeframe, params, notes)
            }
        )
    }
}

@Composable
private fun ExperimentItemCard(
    experiment: Experiment,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (isSelected) Indigo600 else MaterialTheme.colorScheme.outline
    val bgColor = if (isSelected) Indigo50.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("experiment_card_${experiment.experimentId}")
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Indigo50)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = experiment.experimentId,
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo700,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ExperimentStatusBadge(status = experiment.status)
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("btn_delete_experiment_${experiment.experimentId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Experiment",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = experiment.strategyName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = experiment.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TagPill(experiment.instrument)
                TagPill(experiment.timeframe)
                TagPill(experiment.datasetName)
            }
        }
    }
}

@Composable
private fun ExperimentAuditDetailCard(
    experiment: Experiment,
    formattedDate: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("experiment_audit_card"),
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
                    text = "EXPERIMENT AUDIT TRAIL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Indigo600,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
                Text(
                    text = experiment.experimentId,
                    style = MaterialTheme.typography.labelSmall,
                    color = Indigo600,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = experiment.strategyName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Slate100)
            )

            Text(
                text = "Parameters:",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = experiment.parametersSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate700,
                fontSize = 13.sp
            )

            Text(
                text = "Scientist Hypothesis & Notes:",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = experiment.notes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Created: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ExperimentStatusBadge(status: ExperimentStatus) {
    val (bgColor, textColor) = when (status) {
        ExperimentStatus.COMPLETED -> Pair(Indigo50, Indigo600)
        ExperimentStatus.FAILED,
        ExperimentStatus.REJECTED_OVERFIT,
        ExperimentStatus.REJECTED_NEGATIVE_EXPECTANCY,
        ExperimentStatus.REJECTED_EXCESSIVE_DRAWDOWN -> Pair(PolishRose.copy(alpha = 0.12f), PolishRose)
        ExperimentStatus.CONFIGURED,
        ExperimentStatus.QUEUED,
        ExperimentStatus.RUNNING -> Pair(PolishAmber.copy(alpha = 0.12f), PolishAmber)
        else -> Pair(Slate100, Slate600)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.name.replace("REJECTED_", "REJ_"),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun TagPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Slate100)
            .border(1.dp, Slate200, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Slate600,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun CreateExperimentDialog(
    nextId: String,
    onDismiss: () -> Unit,
    onCreate: (stratName: String, instrument: String, tf: String, params: String, notes: String) -> Unit
) {
    var strategyName by remember { mutableStateOf("EMA Pullback Hypothesis") }
    var instrument by remember { mutableStateOf("EUR/USD") }
    var timeframe by remember { mutableStateOf("M15") }
    var parameters by remember { mutableStateOf("EMA_Period: 50, Pullback_Tolerance: 3 pips") }
    var notes by remember {
        mutableStateOf("EUR/USD M15 trend following on 50 EMA pullbacks during London session provides positive expectancy after spread penalties.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Register Research Experiment",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: $nextId",
                    style = MaterialTheme.typography.labelSmall,
                    color = Indigo600,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = strategyName,
                    onValueChange = { strategyName = it },
                    label = { Text("Strategy / Hypothesis Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_exp_name")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = instrument,
                        onValueChange = { instrument = it },
                        label = { Text("Instrument") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_exp_instrument")
                    )

                    OutlinedTextField(
                        value = timeframe,
                        onValueChange = { timeframe = it },
                        label = { Text("Timeframe") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_exp_timeframe")
                    )
                }

                OutlinedTextField(
                    value = parameters,
                    onValueChange = { parameters = it },
                    label = { Text("Parameters Summary") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_exp_params")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Scientific Hypothesis & Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("input_exp_hypothesis")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(strategyName, instrument, timeframe, parameters, notes) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Slate900,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_create_exp")
            ) {
                Text("Register Experiment", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_create_exp")
            ) {
                Text("Cancel", color = Slate600)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
