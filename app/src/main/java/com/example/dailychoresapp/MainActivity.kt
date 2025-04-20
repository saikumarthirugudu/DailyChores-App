package com.example.dailychoresapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dailychoresapp.api.RetrofitInstance
import com.example.dailychoresapp.data.database.AppDatabase
import com.example.dailychoresapp.repository.QuoteRepository
import com.example.dailychoresapp.repository.TaskRepository
import com.example.dailychoresapp.ui.navigation.AppNavGraph
import com.example.dailychoresapp.ui.theme.DailyChoresAppTheme
import com.example.dailychoresapp.viewmodel.QuoteViewModel
import com.example.dailychoresapp.viewmodel.QuoteViewModelFactory
import com.example.dailychoresapp.viewmodel.TaskViewModel
import com.example.dailychoresapp.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DailyChoresAppTheme {
                val navController = rememberNavController()
                val database = AppDatabase.getDatabase(applicationContext)

                val taskRepository = TaskRepository(database.taskDao())
                val quoteRepository = QuoteRepository(RetrofitInstance.api)

                val taskViewModel: TaskViewModel = viewModel(
                    factory = TaskViewModelFactory(application, taskRepository)
                )

                val quoteViewModel: QuoteViewModel = viewModel(
                    factory = QuoteViewModelFactory(quoteRepository)
                )

                AppNavGraph(
                    navController = navController,
                    taskViewModel = taskViewModel,
                    quoteViewModel = quoteViewModel
                )
            }
        }
    }
}