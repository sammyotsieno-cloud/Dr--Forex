package com.example.ui.screens.aichat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.ai.CognitiveTier
import com.example.domain.reasoning.TradeDecision
import com.example.domain.voice.VoiceInputState
import com.example.domain.voice.VoicePlaybackState
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CyanBg
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkPurpleBackground
import com.example.ui.theme.DarkPurpleBorder
import com.example.ui.theme.DarkPurpleCard
import com.example.ui.theme.DarkPurpleHighlight
import com.example.ui.theme.DarkPurpleSurface
import com.example.ui.theme.DarkPurpleSurfaceVariant
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PolishRose
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTraderScreen(
    viewModel: AiTraderViewModel = viewModel(),
    onNavigateToMarketChart: () -> Unit = {},
    onNavigateToResearch: () -> Unit = {},
    onNavigateToExperiments: () -> Unit = {},
    onNavigateToLearning: () -> Unit = {},
    onNavigateToBrokerHub: () -> Unit = {},
    onNavigateToData: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onToggleTheme: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val inputState by viewModel.inputState.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    var showMenu by remember { mutableStateOf(false) }
    var showBgDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Dynamic background brush based on selected theme
    val backgroundModifier = when (uiState.chatBackground) {
        ChatBackgroundTheme.PURPLE_NEBULA -> Modifier.background(
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2B104D),
                    Color(0xFF16092C),
                    DarkPurpleBackground
                ),
                radius = 1400f
            )
        )
        ChatBackgroundTheme.CYBER_GRID -> Modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF090B1E),
                    Color(0xFF140D2D),
                    Color(0xFF0A1024)
                )
            )
        )
        ChatBackgroundTheme.OBSIDIAN_MINIMAL -> Modifier.background(
            Color(0xFF07040D)
        )
        ChatBackgroundTheme.QUANTUM_INDIGO -> Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF190A38),
                    Color(0xFF110626),
                    Color(0xFF090314)
                )
            )
        )
        ChatBackgroundTheme.LIGHT_STUDIO -> Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFAF5FF),
                    Color(0xFFF3E8FF),
                    Color(0xFFEDE9FE)
                )
            )
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(8.dp, shape = CircleShape, spotColor = PurpleAccent)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PurplePrimary, CyanNeon))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Dr. Forex",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldNeon.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .background(EmeraldNeon, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "ONLINE",
                                            color = EmeraldNeon,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${uiState.activeSymbol} • ${uiState.activeTimeframe} • ${uiState.selectedTier.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleAccent,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Quick Theme Toggle
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // 3-Dots Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("three_dots_menu_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(DarkPurpleCard)
                                .border(1.dp, DarkPurpleBorder, RoundedCornerShape(12.dp))
                                .widthIn(min = 220.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("📊 Market & Chart Telemetry", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; onNavigateToMarketChart() },
                                leadingIcon = { Icon(Icons.Default.Analytics, contentDescription = null, tint = CyanGlow) }
                            )
                            DropdownMenuItem(
                                text = { Text("🧪 Research & Strategy Lab", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; onNavigateToResearch() },
                                leadingIcon = { Icon(Icons.Default.Science, contentDescription = null, tint = PurpleAccent) }
                            )
                            DropdownMenuItem(
                                text = { Text("🧬 Experiments & Matrix", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; onNavigateToExperiments() },
                                leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberNeon) }
                            )
                            DropdownMenuItem(
                                text = { Text("🧠 Learning & Memory Center", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; onNavigateToLearning() },
                                leadingIcon = { Icon(Icons.Default.Memory, contentDescription = null, tint = PurpleLight) }
                            )
                            DropdownMenuItem(
                                text = { Text("🏦 Broker Hub & Smart Router", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; onNavigateToBrokerHub() },
                                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = EmeraldNeon) }
                            )
                            DropdownMenuItem(
                                text = { Text("📁 Market Datasets Manager", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; onNavigateToData() },
                                leadingIcon = { Icon(Icons.Default.Dataset, contentDescription = null, tint = CyanNeon) }
                            )
                            DropdownMenuItem(
                                text = { Text("🎨 Custom Chat Background", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; showBgDialog = true },
                                leadingIcon = { Icon(Icons.Default.ColorLens, contentDescription = null, tint = PurpleLight) }
                            )
                            DropdownMenuItem(
                                text = { Text("🗑️ Clear Chat History", color = RoseNeon, fontSize = 13.sp) },
                                onClick = { showMenu = false; viewModel.clearChat() },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = RoseNeon) }
                            )
                            DropdownMenuItem(
                                text = { Text("⚙️ Settings & Voice Cloning", color = Color.White, fontSize = 13.sp) },
                                onClick = { showMenu = false; onNavigateToSettings() },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(backgroundModifier)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Cognitive Tier Selection Pill Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MODE:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )

                        CognitiveTier.entries.forEach { tier ->
                            val isSelected = uiState.selectedTier == tier
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PurplePrimary else Color.Transparent,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) PurpleAccent else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { viewModel.setCognitiveTier(tier) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (tier) {
                                            CognitiveTier.ELI3 -> "👶 ELI3"
                                            CognitiveTier.TRADER -> "📈 Trader"
                                            CognitiveTier.QUANT -> "🔬 Quant"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Trigger Market Analysis Re-evaluation
                        IconButton(
                            onClick = { viewModel.runMarketAnalysis() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Telemetry",
                                tint = CyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        FuturisticChatBubble(
                            message = message,
                            playbackState = playbackState,
                            onSpeak = { viewModel.speakText(message.content) },
                            onStopSpeak = { viewModel.stopVoice() },
                            onQuickPrompt = { prompt -> viewModel.sendUserMessage(prompt) }
                        )
                    }

                    if (uiState.isAnalyzing) {
                        item {
                            AiTypingIndicator()
                        }
                    }
                }

                // Quick Prompt Suggestion Carousel
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val promptSuggestions = listOf(
                        "📊 What is the current EUR/USD setup?",
                        "👶 Explain this setup like I'm 3",
                        "🔬 Show quantitative ATR & risk math",
                        "🛡️ Route trade to optimum broker",
                        "🧠 What patterns have you learned?"
                    )

                    items(promptSuggestions) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkPurpleSurfaceVariant.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, DarkPurpleBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.sendUserMessage(prompt) }
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.bodySmall,
                                color = PurpleLight,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Bottom Input Area with Futuristic Mic & Text Field
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Voice Input Mic Button
                            VoiceMicButton(
                                inputState = inputState,
                                onStartListening = { viewModel.startVoiceInput() }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Text Input Field
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = {
                                    Text(
                                        text = "Ask Dr. Forex anything or tap mic...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PurpleAccent,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedContainerColor = DarkPurpleCard,
                                    unfocusedContainerColor = DarkPurpleCard
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("chat_text_input")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Send Button
                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        viewModel.sendUserMessage(inputText)
                                        inputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .shadow(6.dp, shape = CircleShape, spotColor = PurpleAccent)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(PurplePrimary, PurpleAccent))
                                    )
                                    .testTag("chat_send_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Chat Background Customization Dialog
    if (showBgDialog) {
        AlertDialog(
            onDismissRequest = { showBgDialog = false },
            title = {
                Text(
                    text = "Customize Chat Theme",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChatBackgroundTheme.entries.forEach { bgTheme ->
                        val isSelected = uiState.chatBackground == bgTheme
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PurplePrimary.copy(alpha = 0.3f) else DarkPurpleSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PurpleAccent else DarkPurpleBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.setChatBackground(bgTheme)
                                    showBgDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bgTheme.displayName,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CyanGlow else Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = bgTheme.description,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyanGlow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBgDialog = false }) {
                    Text("Close", color = PurpleLight)
                }
            },
            containerColor = DarkPurpleCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun FuturisticChatBubble(
    message: ChatMessage,
    playbackState: VoicePlaybackState,
    onSpeak: () -> Unit,
    onStopSpeak: () -> Unit,
    onQuickPrompt: (String) -> Unit
) {
    val isUser = message.sender == "USER"
    val isSpeakingThis = playbackState is VoicePlaybackState.Playing && playbackState.text == message.content

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(PurplePrimary, CyanNeon))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 310.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Chat Bubble Container
            Card(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) PurplePrimary.copy(alpha = 0.85f) else DarkPurpleCard.copy(alpha = 0.95f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isUser) PurpleAccent else DarkPurpleBorder
                ),
                modifier = Modifier.shadow(
                    elevation = if (isUser) 6.dp else 4.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = if (isUser) PurpleAccent else Color.Transparent
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header Tag for AI Messages
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkPurpleSurfaceVariant,
                                border = BorderStroke(1.dp, DarkPurpleHighlight)
                            ) {
                                Text(
                                    text = when (message.tier) {
                                        CognitiveTier.ELI3 -> "👶 ELI3 Mode"
                                        CognitiveTier.TRADER -> "📈 Trader Mode"
                                        CognitiveTier.QUANT -> "🔬 Quant Mode"
                                    },
                                    fontSize = 9.sp,
                                    color = PurpleLight,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            // Speech Playback Button
                            IconButton(
                                onClick = {
                                    if (isSpeakingThis) onStopSpeak() else onSpeak()
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeakingThis) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Read Aloud",
                                    tint = if (isSpeakingThis) RoseNeon else CyanGlow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Message Content
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )

                    // Real-Time Waveform Equalizer when reading aloud
                    if (isSpeakingThis) {
                        Spacer(modifier = Modifier.height(10.dp))
                        VoiceWaveformBar()
                    }
                }
            }

            // Quick Follow-up Action Chips for AI Messages
            if (!isUser) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkPurpleSurfaceVariant,
                        border = BorderStroke(1.dp, DarkPurpleBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onQuickPrompt("Explain this simpler like I'm 3 👶") }
                    ) {
                        Text(
                            text = "👶 Simpler",
                            color = PurpleLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkPurpleSurfaceVariant,
                        border = BorderStroke(1.dp, DarkPurpleBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onQuickPrompt("Show quantitative mathematical proof 🔬") }
                    ) {
                        Text(
                            text = "🔬 Quant Math",
                            color = CyanGlow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(PurpleAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "YOU",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun VoiceMicButton(
    inputState: VoiceInputState,
    onStartListening: () -> Unit
) {
    val isListening = inputState is VoiceInputState.Listening

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    IconButton(
        onClick = onStartListening,
        modifier = Modifier
            .size(46.dp)
            .scale(pulseScale)
            .shadow(
                elevation = if (isListening) 12.dp else 4.dp,
                shape = CircleShape,
                spotColor = if (isListening) RoseNeon else PurpleAccent
            )
            .clip(CircleShape)
            .background(
                if (isListening) Brush.linearGradient(listOf(RoseNeon, AmberNeon))
                else Brush.linearGradient(listOf(DarkPurpleSurfaceVariant, DarkPurpleHighlight))
            )
            .border(
                1.5.dp,
                if (isListening) RoseNeon else DarkPurpleBorder,
                CircleShape
            )
            .testTag("chat_mic_btn")
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Input",
            tint = if (isListening) Color.White else CyanGlow,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun VoiceWaveformBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse), label = "b1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "b2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse), label = "b3"
    )
    val bar4 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse), label = "b4"
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyanBg.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Voice Synthesizer Active",
                color = CyanGlow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            listOf(bar1, bar2, bar3, bar4).forEach { heightRatio ->
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height((16 * heightRatio).coerceAtLeast(4f).dp)
                        .clip(CircleShape)
                        .background(CyanGlow)
                )
            }
        }
    }
}

@Composable
fun AiTypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkPurpleCard,
            border = BorderStroke(1.dp, DarkPurpleBorder),
            modifier = Modifier.padding(start = 42.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = PurpleAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dr. Forex is computing market confluence...",
                    color = PurpleLight,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
