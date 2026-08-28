package com.example.ui.screens.broker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.broker.AccountEnvironment
import com.example.domain.broker.BrokerAccount
import com.example.domain.broker.BrokerPlatform
import com.example.domain.broker.SmartOrderRoutingEngine
import com.example.ui.theme.AmberNeon
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
fun BrokerHubScreen(
    onNavigateBack: () -> Unit
) {
    var accounts by remember { mutableStateOf(SmartOrderRoutingEngine.getDefaultAccounts()) }
    var preferDemo by remember { mutableStateOf(true) }
    var showExecutedDialog by remember { mutableStateOf(false) }
    var executedMessage by remember { mutableStateOf("") }

    val routingEvaluations = remember(accounts, preferDemo) {
        SmartOrderRoutingEngine.evaluateRouting(
            symbol = "EUR/USD",
            lotSize = 0.50,
            accounts = accounts,
            preferDemo = preferDemo
        )
    }

    val bestRoute = routingEvaluations.find { it.isOptimal } ?: routingEvaluations.firstOrNull()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Broker Hub & Smart Router",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Layer 1/5 • Multi-Gateway Order Routing",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleAccent,
                            fontSize = 10.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("broker_back_btn")) {
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Environment Mode Toggle (Demo vs Live)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Demo Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (preferDemo) Brush.horizontalGradient(
                                        listOf(PurplePrimary, PurpleAccent)
                                    ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                )
                                .clickable { preferDemo = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🧪 Demo / Research Lab",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (preferDemo) FontWeight.Bold else FontWeight.Medium,
                                color = if (preferDemo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Live Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (!preferDemo) Brush.horizontalGradient(
                                        listOf(RoseNeon, Color(0xFFE11D48))
                                    ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                )
                                .clickable { preferDemo = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🛡️ Live / Real Capital",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (!preferDemo) FontWeight.Bold else FontWeight.Medium,
                                color = if (!preferDemo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 1-Tap Permission & Risk Approval Simulator Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (preferDemo) DarkPurpleCard else Color(0xFF1E0D1B)
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (preferDemo) Brush.horizontalGradient(listOf(PurpleAccent, CyanGlow))
                        else Brush.horizontalGradient(listOf(RoseNeon, AmberNeon))
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("trade_approval_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (preferDemo) PurplePrimary.copy(alpha = 0.3f) else RoseNeon.copy(alpha = 0.3f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (preferDemo) Icons.Default.Security else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (preferDemo) CyanGlow else RoseNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "1-TAP PERMISSION & RISK GATE",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (preferDemo) CyanGlow else RoseNeon,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = if (preferDemo) "Zero financial risk • Forward sandbox" else "Requires biometric user confirmation",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (preferDemo) EmeraldNeon.copy(alpha = 0.15f) else AmberNeon.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, if (preferDemo) EmeraldNeon else AmberNeon)
                            ) {
                                Text(
                                    text = "BUY 0.50 LOT",
                                    color = if (preferDemo) EmeraldNeon else AmberNeon,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Order Metrics Matrix
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "SYMBOL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "EUR/USD", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "ENTRY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "1.08500", fontWeight = FontWeight.Bold, color = CyanGlow, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "STOP LOSS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "1.08200 (-30p)", fontWeight = FontWeight.Bold, color = RoseNeon, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "MAX RISK", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "$150 (1.0%)", fontWeight = FontWeight.Bold, color = EmeraldNeon, fontSize = 13.sp)
                                }
                            }
                        }

                        // Optimal Route Banner
                        if (bestRoute != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkPurpleSurfaceVariant,
                                border = BorderStroke(1.dp, DarkPurpleBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Route,
                                        contentDescription = null,
                                        tint = PurpleAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Optimum Route: ${bestRoute.brokerAccount.name}",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = bestRoute.recommendationReason,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldNeon.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "${bestRoute.spreadPips}p / ${bestRoute.executionLatencyMs}ms",
                                            color = EmeraldNeon,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Action Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    executedMessage = "✅ Successfully executed 0.50 Lots EUR/USD on ${bestRoute?.brokerAccount?.name} (${if (preferDemo) "DEMO" else "LIVE"}). Position ID: #TRD-${System.currentTimeMillis() % 100000}"
                                    showExecutedDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (preferDemo) PurplePrimary else EmeraldNeon
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("approve_trade_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (preferDemo) "Approve Demo Order" else "Approve Live Order",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Execution Feedback Alert
            if (showExecutedDialog) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = EmeraldNeon.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, EmeraldNeon),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldNeon,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Order Placed & Confirmed",
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldNeon,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = executedMessage,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(onClick = { showExecutedDialog = false }) {
                                Text(text = "✕", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Connected Broker Accounts List Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CONNECTED BROKER GATEWAYS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkPurpleBorder,
                        border = BorderStroke(1.dp, DarkPurpleHighlight)
                    ) {
                        Text(
                            text = "${accounts.size} Active Gateways",
                            color = PurpleLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Broker Account Cards
            items(accounts) { acc ->
                val eval = routingEvaluations.find { it.brokerAccount.id == acc.id }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (eval?.isOptimal == true) PurpleAccent else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(PurplePrimary.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = PurpleAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = acc.name,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${acc.platform.protocol} • #${acc.accountNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            if (eval?.isOptimal == true) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PurplePrimary.copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, PurpleAccent)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = CyanGlow,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "AI OPTIMAL",
                                            fontWeight = FontWeight.Bold,
                                            color = CyanGlow,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Account Financial Telemetry
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "EQUITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                Text(
                                    text = "$${String.format("%,.2f", acc.equity)}",
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldNeon,
                                    fontSize = 14.sp
                                )
                            }
                            Column {
                                Text(text = "FREE MARGIN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                Text(
                                    text = "$${String.format("%,.2f", acc.freeMargin)}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            }
                            Column {
                                Text(text = "LATENCY / PING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                Text(
                                    text = "${acc.pingMs} ms (LD4)",
                                    fontWeight = FontWeight.Bold,
                                    color = if (acc.pingMs < 20) EmeraldNeon else AmberNeon,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Emergency Global Killswitch Button
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A0D15)
                    ),
                    border = BorderStroke(1.dp, RoseNeon.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emergency Killswitch",
                                fontWeight = FontWeight.Bold,
                                color = RoseNeon,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Instantly closes all open positions across all connected brokers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = {
                                executedMessage = "⚠️ Emergency Killswitch triggered: All positions across cTrader, MT5 and OANDA closed."
                                showExecutedDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoseNeon),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "CLOSE ALL", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
