package com.example.dailychoresapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dailychoresapp.data.model.Task
import com.example.dailychoresapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavController,
    taskId: Int,
    taskViewModel: TaskViewModel = viewModel()
) {
    val taskToEdit by taskViewModel.getTaskById(taskId).observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (taskToEdit == null) {
                Text("Loading...", modifier = Modifier.padding(16.dp))
                return@Column
            }

            var taskName by remember { mutableStateOf(taskToEdit!!.title) }
            var taskDescription by remember { mutableStateOf(taskToEdit!!.description) }

            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Task Description") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    taskViewModel.updateTask(
                        Task(id = taskId, title = taskName, description = taskDescription)
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Update Task")
            }
        }
    }
}
