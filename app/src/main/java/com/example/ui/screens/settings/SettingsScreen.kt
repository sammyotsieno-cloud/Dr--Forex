package com.example.ui.screens.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.VoiceConfigEntity
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkPurpleBorder
import com.example.ui.theme.DarkPurpleCard
import com.example.ui.theme.DarkPurpleHighlight
import com.example.ui.theme.DarkPurpleSurfaceVariant
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    themeMode: AppThemeMode = AppThemeMode.DARK,
    onThemeChange: (AppThemeMode) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val currentConfig by viewModel.configFlow.collectAsStateWithLifecycle()
    val voiceConfig by viewModel.voiceConfigFlow.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var baseCurrency by remember { mutableStateOf(currentConfig.baseCurrency) }
    var initialCapital by remember { mutableStateOf(currentConfig.initialCapital.toInt().toString()) }
    var riskPercent by remember { mutableStateOf(currentConfig.defaultRiskPerTradePercent.toString()) }
    var instrument by remember { mutableStateOf(currentConfig.defaultInstrument) }
    var executionTf by remember { mutableStateOf(currentConfig.defaultExecutionTimeframe) }
    var spread by remember { mutableStateOf(currentConfig.defaultSpreadPips.toString()) }

    var voiceMode by remember { mutableStateOf(voiceConfig.mode) }
    var voiceApiKey by remember { mutableStateOf(voiceConfig.apiKey) }
    var voiceId by remember { mutableStateOf(voiceConfig.voiceId) }
    var speechRate by remember { mutableFloatStateOf(voiceConfig.speechRate) }
    var pitch by remember { mutableFloatStateOf(voiceConfig.pitch) }
    var autoPlayVoice by remember { mutableStateOf(voiceConfig.autoPlayVoice) }

    LaunchedEffect(currentConfig) {
        baseCurrency = currentConfig.baseCurrency
        initialCapital = currentConfig.initialCapital.toInt().toString()
        riskPercent = currentConfig.defaultRiskPerTradePercent.toString()
        instrument = currentConfig.defaultInstrument
        executionTf = currentConfig.defaultExecutionTimeframe
        spread = currentConfig.defaultSpreadPips.toString()
    }

    LaunchedEffect(voiceConfig) {
        voiceMode = voiceConfig.mode
        voiceApiKey = voiceConfig.apiKey
        voiceId = voiceConfig.voiceId
        speechRate = voiceConfig.speechRate
        pitch = voiceConfig.pitch
        autoPlayVoice = voiceConfig.autoPlayVoice
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings & Voice Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Themes • Neural Voice Cloning • Risk Invariants",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleAccent,
                            fontSize = 10.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("settings_back_btn")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("settings_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Mode Selector Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = PurpleAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "App Visual Theme",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple(AppThemeMode.DARK, "Refined Dark Purple", Icons.Default.DarkMode),
                                Triple(AppThemeMode.LIGHT, "Lilac Studio (Light)", Icons.Default.LightMode),
                                Triple(AppThemeMode.SYSTEM, "System Auto", Icons.Default.AutoAwesome)
                            ).forEach { (mode, label, icon) ->
                                val isSelected = themeMode == mode
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) PurplePrimary.copy(alpha = 0.25f) else DarkPurpleSurfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) PurpleAccent else DarkPurpleBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onThemeChange(mode) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) CyanGlow else PurpleLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Voice Studio & Neural Cloning Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = CyanGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Voice Studio & Neural Voice Cloning",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }

                        // Mode Selector (System TTS vs Neural Clone)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "SYSTEM_TTS" to "🔊 Offline Android TTS",
                                "CUSTOM_VOICE" to "✨ Cloned Voice (API)"
                            ).forEach { (mode, title) ->
                                val isSelected = voiceMode == mode
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) PurplePrimary else DarkPurpleSurfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelected) PurpleAccent else DarkPurpleBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { voiceMode = mode }
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                                    )
                                }
                            }
                        }

                        if (voiceMode == "CUSTOM_VOICE") {
                            OutlinedTextField(
                                value = voiceApiKey,
                                onValueChange = { voiceApiKey = it },
                                label = { Text("Voice Cloning API Key") },
                                placeholder = { Text("e.g. sk_elevenlabs_...") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = voiceId,
                                onValueChange = { voiceId = it },
                                label = { Text("Cloned Voice ID") },
                                placeholder = { Text("e.g. 21m00Tcm4TlvDq8ikWAM") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Speech Rate & Pitch
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Speech Rate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${String.format("%.2f", speechRate)}x", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Slider(
                                value = speechRate,
                                onValueChange = { speechRate = it },
                                valueRange = 0.5f..2.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyanGlow,
                                    activeTrackColor = PurpleAccent
                                )
                            )
                        }

                        // Auto-play toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Auto-Read AI Insights Aloud", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "Automatically synthesizes speech for chat answers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                            Switch(
                                checked = autoPlayVoice,
                                onCheckedChange = { autoPlayVoice = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PurplePrimary
                                )
                            )
                        }

                        // Test Voice Button
                        OutlinedButton(
                            onClick = {
                                viewModel.testVoice(
                                    text = "Hello! I am Dr. Forex. Voice synthesis is calibrated and running cleanly.",
                                    voiceConfig = VoiceConfigEntity(
                                        mode = voiceMode,
                                        apiKey = voiceApiKey,
                                        voiceId = voiceId,
                                        speechRate = speechRate,
                                        pitch = pitch
                                    )
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Audition Voice Profile", color = PurpleLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Risk & Laboratory Parameters Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = EmeraldNeon,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Risk Invariant Engine",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = baseCurrency,
                                onValueChange = { baseCurrency = it },
                                label = { Text("Currency") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = initialCapital,
                                onValueChange = { initialCapital = it },
                                label = { Text("Capital ($)") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = riskPercent,
                                onValueChange = { riskPercent = it },
                                label = { Text("Risk / Trade (%)") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = spread,
                                onValueChange = { spread = it },
                                label = { Text("Spread (pips)") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Save Confirmation Banner
            if (state.isSaved) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldNeon.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, EmeraldNeon),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = state.saveMessage, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Bottom Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetToDefaults() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Reset Defaults")
                    }

                    Button(
                        onClick = {
                            viewModel.saveConfiguration(
                                baseCurrency = baseCurrency,
                                initialCapital = initialCapital.toDoubleOrNull() ?: 20000.0,
                                riskPercent = riskPercent.toDoubleOrNull() ?: 1.0,
                                instrument = instrument,
                                timeframe = executionTf,
                                contextTimeframes = listOf("H1", "H4"),
                                spread = spread.toDoubleOrNull() ?: 1.5,
                                slippage = 0.5,
                                voiceMode = voiceMode,
                                voiceApiKey = voiceApiKey,
                                voiceId = voiceId,
                                speechRate = speechRate,
                                pitch = pitch,
                                autoPlayVoice = autoPlayVoice
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Save All", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
