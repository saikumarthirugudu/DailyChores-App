package com.example.dailychoresapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dailychoresapp.ui.screens.AddTaskScreen
import com.example.dailychoresapp.ui.screens.HomeScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object AddTask : Screen("add_task_screen")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.AddTask.route) { AddTaskScreen(navController) }
    }
}
