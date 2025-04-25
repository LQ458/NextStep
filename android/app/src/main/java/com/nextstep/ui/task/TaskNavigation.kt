package com.nextstep.ui.task

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class TaskScreen(val route: String) {
    object TaskList : TaskScreen("taskList")
    object AddTask : TaskScreen("addTask")
    object Calendar : TaskScreen("calendar")
    object Projects : TaskScreen("projects")
    object Search : TaskScreen("search")
}

@Composable
fun TaskNavigation(
    viewModel: TaskViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    onOpenCalendar: () -> Unit = { navController.navigate(TaskScreen.Calendar.route) },
    onOpenProjects: () -> Unit = { navController.navigate(TaskScreen.Projects.route) },
    onOpenSearch: () -> Unit = { navController.navigate(TaskScreen.Search.route) }
) {
    NavHost(
        navController = navController,
        startDestination = TaskScreen.TaskList.route
    ) {
        composable(TaskScreen.TaskList.route) {
            TaskListScreen(
                viewModel = viewModel,
                onAddTask = {
                    navController.navigate(TaskScreen.AddTask.route)
                },
                onOpenCalendar = onOpenCalendar,
                onOpenProjects = onOpenProjects,
                onOpenSearch = onOpenSearch
            )
        }
        composable(TaskScreen.AddTask.route) {
            AddTaskScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 