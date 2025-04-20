package com.example.dailychoresapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dailychoresapp.ui.screens.*
import com.example.dailychoresapp.viewmodel.QuoteViewModel
import com.example.dailychoresapp.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class Screen(val route: String) {
    data object Splash : Screen("splash_screen")
    data object SignUp : Screen("signup_screen")
    data object Login : Screen("login_screen")
    data object ForgotPassword : Screen("forgot_password_screen")
    data object Home : Screen("home_screen")
    data object Completed : Screen("completed_screen")
    data object Stats : Screen("stats_screen")
    data object AddTask : Screen("add_task_screen")
    data object EditTask : Screen("edit_task_screen/{taskId}") {
        fun passTaskId(taskId: Int) = "edit_task_screen/$taskId"
    }
    data object TaskDetailScreen : Screen("task_detail_screen/{taskId}") {
        fun passTaskId(taskId: Int) = "task_detail_screen/$taskId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    quoteViewModel: QuoteViewModel
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    NavHost(navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUp = { email, password ->
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                user?.sendEmailVerification()
                                navController.navigate(Screen.Login.route)
                            }
                        }
                },
                onLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                },
                onSignUp = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onResetPassword = { email ->
                    if (email.isEmpty()) return@ForgotPasswordScreen
                    firestore.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful && !task.result.isEmpty) {
                                auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener { resetTask ->
                                        if (resetTask.isSuccessful) {
                                            navController.navigate(Screen.Login.route)
                                        }
                                    }
                            }
                        }
                },
                onBackToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Home.route) {
            MainScreen(navController) {
                HomeScreen(navController, taskViewModel, quoteViewModel)
            }
        }

        composable(Screen.Completed.route) {
            MainScreen(navController) {
                CompletedScreen(navController, taskViewModel)
            }
        }

        composable(Screen.Stats.route) {
            MainScreen(navController) {
                StatsScreen(taskViewModel)
            }
        }

        composable(Screen.AddTask.route) {
            AddTaskScreen(navController, taskViewModel)
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            if (taskId != -1) {
                EditTaskScreen(navController, taskId, taskViewModel)
            }
        }

        composable(
            route = Screen.TaskDetailScreen.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            if (taskId != -1) {
                TaskDetailScreen(taskId = taskId, navController = navController, taskViewModel = taskViewModel)
            }
        }
    }
}