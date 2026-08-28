package com.example.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkPurpleBackground
import com.example.ui.theme.DarkPurpleBorder
import com.example.ui.theme.DarkPurpleCard
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(
    onSplashFinished: () -> Unit
) {
    val letters = listOf("D", "r", ".", " ", "F", "o", "r", "e", "x")
    val letterAnimations = letters.indices.map {
        remember { Animatable(0f) }
    }

    var showSubtitle by remember { mutableStateOf(false) }
    var showLayers by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val systemSteps = listOf(
        "⬡ Layer 1: Market Telemetry & Multi-Broker Feeds...",
        "⬡ Layer 2: Quantitative Research & Backtesting...",
        "⬡ Layer 3: Knowledge Memory & Pattern Discovery...",
        "⬡ Layer 4: Multi-Tier Cognitive AI & Voice Ready...",
        "✦ Neural Trading System Fully Initialized"
    )

    // Infinite breathing glow
    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Choreographed letter entrance animation
    LaunchedEffect(Unit) {
        delay(200)
        letters.indices.forEach { index ->
            delay(75)
            letterAnimations[index].animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
        delay(150)
        showSubtitle = true
        showLayers = true

        // Step through system checks
        for (i in systemSteps.indices) {
            currentStepIndex = i
            delay(400)
        }

        delay(400)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2E1256),
                        Color(0xFF16092B),
                        DarkPurpleBackground
                    ),
                    radius = 1200f
                )
            )
            .clickable { onSplashFinished() }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Background Ambient Glow Orbs
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(pulseScale)
                .alpha(glowAlpha * 0.4f)
                .blur(60.dp)
                .background(PurplePrimary, shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = 80.dp, y = (-100).dp)
                .alpha(glowAlpha * 0.3f)
                .blur(50.dp)
                .background(CyanNeon, shape = CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Glowing AI Brain Orb
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(pulseScale)
                    .shadow(24.dp, shape = CircleShape, spotColor = PurpleAccent)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF3B1569),
                                Color(0xFF1B0B34)
                            )
                        )
                    )
                    .border(2.dp, Brush.horizontalGradient(listOf(PurpleAccent, CyanGlow)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Dr. Forex Brain",
                    tint = PurpleLight,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Choreographed Letters: "Dr. Forex"
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                letters.forEachIndexed { index, char ->
                    val animVal = letterAnimations[index].value
                    val isDr = index < 3
                    val charColor = if (isDr) CyanGlow else PurpleLight

                    Text(
                        text = if (char == " ") "\u00A0" else char,
                        color = charColor,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = ((1f - animVal) * 35).toInt()
                                )
                            }
                            .alpha(animVal)
                            .scale(0.5f + (animVal * 0.5f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle with Fade
            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 20 }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "AUTONOMOUS QUANTITATIVE MARKET AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Futuristic Initialization Ticker
            AnimatedVisibility(
                visible = showLayers,
                enter = fadeIn(tween(500))
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkPurpleCard.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkPurpleBorder),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentStepIndex == 4) Icons.Default.Bolt else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (currentStepIndex == 4) EmeraldNeon else CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = systemSteps[currentStepIndex],
                            style = MaterialTheme.typography.bodySmall,
                            color = if (currentStepIndex == 4) EmeraldNeon else Color(0xFFD8B4FE),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Skip Hint
            Text(
                text = "Tap anywhere to begin",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
