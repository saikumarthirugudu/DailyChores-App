package com.example.dailychoresapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    data object Completed : BottomNavItem("completed", "Completed", Icons.Filled.CheckCircle)
    data object Stats : BottomNavItem("stats", "Stats", Icons.Filled.BarChart)
}
