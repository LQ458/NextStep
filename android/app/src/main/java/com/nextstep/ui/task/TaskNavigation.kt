package com.nextstep.ui.task

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class TaskScreen(val route: String) {
    object TaskList : TaskScreen("taskList")
    object AddTask : TaskScreen("addTask")
}

@Composable
fun TaskNavigation(
    viewModel: TaskViewModel,
    navController: NavHostController = rememberNavController()
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
                }
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