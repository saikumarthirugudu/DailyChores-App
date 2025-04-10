package com.example.dailychoresapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dailychoresapp.ui.screens.AddTaskScreen
import com.example.dailychoresapp.ui.screens.EditTaskScreen
import com.example.dailychoresapp.ui.screens.HomeScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object AddTask : Screen("add_task_screen")
    data object EditTask : Screen("edit_task_screen/{taskId}") {
        fun passTaskId(taskId: Int) = "edit_task_screen/$taskId"
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.AddTask.route) { AddTaskScreen(navController) }

        // Task Screen with Task ID
        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            if (taskId != -1) {
                EditTaskScreen(navController, taskId)
            }
        }
    }
}
