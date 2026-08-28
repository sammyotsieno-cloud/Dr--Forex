package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.aichat.AiTraderScreen
import com.example.ui.screens.broker.BrokerHubScreen
import com.example.ui.screens.data.DataScreen
import com.example.ui.screens.experiments.ExperimentsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.learning.LearningCenterScreen
import com.example.ui.screens.research.ResearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.AnimatedSplashScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.DrForexTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val tag: String) {
    object Splash : Screen("splash", "Splash", Icons.Default.Psychology, "nav_splash")
    object AiTrader : Screen("ai_trader", "Dr. Forex AI", Icons.Default.Psychology, "nav_ai_trader")
    object MarketChart : Screen("market_chart", "Market Telemetry", Icons.Default.Analytics, "nav_market")
    object Research : Screen("research", "Research Lab", Icons.Default.Science, "nav_research")
    object Experiments : Screen("experiments", "Experiments", Icons.Default.Science, "nav_experiments")
    object Learning : Screen("learning", "Knowledge Memory", Icons.Default.Memory, "nav_learning")
    object BrokerHub : Screen("broker_hub", "Broker Hub", Icons.Default.AccountBalance, "nav_broker")
    object Data : Screen("data", "Datasets", Icons.Default.Dataset, "nav_data")
    object Settings : Screen("settings", "Settings", Icons.Default.Settings, "nav_settings")
}

@Composable
fun DrForexApp() {
    var themeMode by remember { mutableStateOf(AppThemeMode.DARK) }

    DrForexTheme(themeMode = themeMode) {
        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.fillMaxSize()
            ) {
                // Animated Choreographed Splash Screen
                composable(Screen.Splash.route) {
                    AnimatedSplashScreen(
                        onSplashFinished = {
                            navController.navigate(Screen.AiTrader.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Primary Home Interface: Dedicated AI Chat
                composable(Screen.AiTrader.route) {
                    AiTraderScreen(
                        onNavigateToMarketChart = { navController.navigate(Screen.MarketChart.route) },
                        onNavigateToResearch = { navController.navigate(Screen.Research.route) },
                        onNavigateToExperiments = { navController.navigate(Screen.Experiments.route) },
                        onNavigateToLearning = { navController.navigate(Screen.Learning.route) },
                        onNavigateToBrokerHub = { navController.navigate(Screen.BrokerHub.route) },
                        onNavigateToData = { navController.navigate(Screen.Data.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                        onToggleTheme = {
                            themeMode = if (themeMode == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
                        }
                    )
                }

                // Layer 1: Market & Chart Telemetry Screen
                composable(Screen.MarketChart.route) {
                    HomeScreen(
                        onNavigateToAiTrader = { navController.navigate(Screen.AiTrader.route) },
                        onNavigateToResearch = { navController.navigate(Screen.Research.route) },
                        onNavigateToData = { navController.navigate(Screen.Data.route) },
                        onNavigateToExperiments = { navController.navigate(Screen.Experiments.route) }
                    )
                }

                // Layer 2: Research & Strategy Lab
                composable(Screen.Research.route) {
                    ResearchScreen()
                }

                // Layer 2/3: Validation Matrix & Experiments
                composable(Screen.Experiments.route) {
                    ExperimentsScreen()
                }

                // Layer 3: Knowledge Memory & Learning Center
                composable(Screen.Learning.route) {
                    LearningCenterScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Layer 1/5: Broker Hub & Smart Order Router
                composable(Screen.BrokerHub.route) {
                    BrokerHubScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Layer 1: Datasets Manager
                composable(Screen.Data.route) {
                    DataScreen()
                }

                // Layer 5: Settings & Customization
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        themeMode = themeMode,
                        onThemeChange = { newMode -> themeMode = newMode },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
