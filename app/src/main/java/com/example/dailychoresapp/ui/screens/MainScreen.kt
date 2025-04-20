package com.example.dailychoresapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.dailychoresapp.ui.components.BottomNavBar

@Composable
fun MainScreen(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
