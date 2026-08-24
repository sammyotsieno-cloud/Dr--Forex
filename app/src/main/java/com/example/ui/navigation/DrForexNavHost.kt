package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.data.DataScreen
import com.example.ui.screens.experiments.ExperimentsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.research.ResearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val tag: String) {
    object Home : Screen("home", "Home", Icons.Default.Home, "nav_home")
    object Research : Screen("research", "Pipeline", Icons.Default.Analytics, "nav_research")
    object Data : Screen("data", "Data", Icons.Default.Dataset, "nav_data")
    object Experiments : Screen("experiments", "Experiments", Icons.Default.Science, "nav_experiments")
    object Settings : Screen("settings", "Settings", Icons.Default.Settings, "nav_settings")
}

@Composable
fun DrForexApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        Screen.Home,
        Screen.Research,
        Screen.Data,
        Screen.Experiments,
        Screen.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .shadow(12.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ),
                color = MaterialTheme.colorScheme.surface
            ) {
                NavigationBar(
                    modifier = Modifier
                        .testTag("bottom_nav_bar")
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp
                ) {
                    navigationItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            modifier = Modifier.testTag(screen.tag),
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                )
                            },
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Indigo600,
                                selectedTextColor = Indigo600,
                                indicatorColor = Indigo50,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToResearch = {
                        navController.navigate(Screen.Research.route)
                    },
                    onNavigateToData = {
                        navController.navigate(Screen.Data.route)
                    },
                    onNavigateToExperiments = {
                        navController.navigate(Screen.Experiments.route)
                    }
                )
            }

            composable(Screen.Research.route) {
                ResearchScreen()
            }

            composable(Screen.Data.route) {
                DataScreen()
            }

            composable(Screen.Experiments.route) {
                ExperimentsScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
