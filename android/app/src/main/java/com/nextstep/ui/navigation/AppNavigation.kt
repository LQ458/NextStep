package com.nextstep.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nextstep.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Tasks : Screen(
        route = "tasks",
        title = "任务",
        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tasks") }
    )
    
    object Projects : Screen(
        route = "projects",
        title = "项目",
        icon = { Icon(Icons.Default.Folder, contentDescription = "Projects") }
    )
    
    object Calendar : Screen(
        route = "calendar",
        title = "日历",
        icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") }
    )
    
    object Statistics : Screen(
        route = "statistics",
        title = "统计",
        icon = { Icon(Icons.Default.BarChart, contentDescription = "Statistics") }
    )
    
    object Settings : Screen(
        route = "settings",
        title = "设置",
        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Tasks,
        Screen.Projects,
        Screen.Calendar,
        Screen.Statistics,
        Screen.Settings
    )
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Tasks) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { screen.icon() },
                        label = { Text(screen.title) },
                        selected = selectedScreen == screen,
                        onClick = {
                            selectedScreen = screen
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tasks.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Tasks.route) {
                TasksScreen(
                    onTaskClick = { /* Navigate to task detail */ },
                    onAddTaskClick = { /* Navigate to add task */ }
                )
            }
            
            composable(Screen.Projects.route) {
                ProjectsScreen(
                    onProjectClick = { /* Navigate to project detail */ },
                    onAddProjectClick = { /* Navigate to add project */ }
                )
            }
            
            composable(Screen.Calendar.route) {
                TaskCalendarScreen(
                    onTaskClick = { /* Navigate to task detail */ }
                )
            }
            
            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
} 