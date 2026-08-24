package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ScientificPrincipleBanner
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Indigo700
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val currentConfig by viewModel.configFlow.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var baseCurrency by remember { mutableStateOf(currentConfig.baseCurrency) }
    var initialCapital by remember { mutableStateOf(currentConfig.initialCapital.toString()) }
    var riskPercent by remember { mutableStateOf(currentConfig.defaultRiskPerTradePercent.toString()) }
    var instrument by remember { mutableStateOf(currentConfig.defaultInstrument) }
    var executionTf by remember { mutableStateOf(currentConfig.defaultExecutionTimeframe) }
    var contextTfs by remember { mutableStateOf(currentConfig.contextTimeframes.joinToString(", ")) }
    var spread by remember { mutableStateOf(currentConfig.defaultSpreadPips.toString()) }
    var slippage by remember { mutableStateOf(currentConfig.defaultSlippagePips.toString()) }

    LaunchedEffect(currentConfig) {
        baseCurrency = currentConfig.baseCurrency
        initialCapital = currentConfig.initialCapital.toInt().toString()
        riskPercent = currentConfig.defaultRiskPerTradePercent.toString()
        instrument = currentConfig.defaultInstrument
        executionTf = currentConfig.defaultExecutionTimeframe
        contextTfs = currentConfig.contextTimeframes.joinToString(", ")
        spread = currentConfig.defaultSpreadPips.toString()
        slippage = currentConfig.defaultSlippagePips.toString()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Research Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "GLOBAL PARAMETERS & SCIENTIFIC ASSUMPTIONS",
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
                title = "TRANSACTION COST & SIZING MANDATE",
                description = "Backtest execution without spread, slippage, and strict risk sizing produces illusory profits. These parameters will be strictly enforced in subsequent research phases."
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "CAPITAL & RISK PARAMETERS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo600,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )

                    OutlinedTextField(
                        value = baseCurrency,
                        onValueChange = { baseCurrency = it },
                        label = { Text("Base Currency") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_base_currency")
                    )

                    OutlinedTextField(
                        value = initialCapital,
                        onValueChange = { initialCapital = it },
                        label = { Text("Initial Research Capital") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_initial_capital")
                    )

                    OutlinedTextField(
                        value = riskPercent,
                        onValueChange = { riskPercent = it },
                        label = { Text("Default Risk Per Trade (%)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_risk_percent")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "INSTRUMENT & TIMEFRAME DEFAULTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo600,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )

                    OutlinedTextField(
                        value = instrument,
                        onValueChange = { instrument = it },
                        label = { Text("Default Instrument") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_default_instrument")
                    )

                    OutlinedTextField(
                        value = executionTf,
                        onValueChange = { executionTf = it },
                        label = { Text("Default Execution Timeframe") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_execution_tf")
                    )

                    OutlinedTextField(
                        value = contextTfs,
                        onValueChange = { contextTfs = it },
                        label = { Text("Context Timeframes (CSV)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_context_tfs")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "MARKET FRICTION ASSUMPTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo600,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = spread,
                            onValueChange = { spread = it },
                            label = { Text("Spread (Pips)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_spread_pips")
                        )

                        OutlinedTextField(
                            value = slippage,
                            onValueChange = { slippage = it },
                            label = { Text("Slippage (Pips)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_slippage_pips")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val capitalVal = initialCapital.toDoubleOrNull() ?: 20000.0
                                val riskVal = riskPercent.toDoubleOrNull() ?: 1.0
                                val tfList = contextTfs.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                val spreadVal = spread.toDoubleOrNull() ?: 1.5
                                val slippageVal = slippage.toDoubleOrNull() ?: 0.5

                                viewModel.saveConfiguration(
                                    baseCurrency = baseCurrency,
                                    initialCapital = capitalVal,
                                    riskPercent = riskVal,
                                    instrument = instrument,
                                    timeframe = executionTf,
                                    contextTimeframes = tfList,
                                    spread = spreadVal,
                                    slippage = slippageVal
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_save_settings"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Indigo600,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Config", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetToDefaults() },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_reset_defaults"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate800),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (state.isSaved) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = PolishEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = state.saveMessage,
                                style = MaterialTheme.typography.labelSmall,
                                color = PolishEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
