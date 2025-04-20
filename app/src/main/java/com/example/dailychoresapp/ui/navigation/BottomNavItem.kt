package com.example.dailychoresapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem("home_screen", Icons.Filled.Home, "Home")
    data object Completed : BottomNavItem("completed_screen", Icons.Filled.CheckCircle, "Completed")
    data object Stats : BottomNavItem("stats_screen", Icons.Filled.BarChart, "Stats")
}