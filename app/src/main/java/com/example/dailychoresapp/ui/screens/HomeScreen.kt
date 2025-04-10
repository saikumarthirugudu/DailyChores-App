package com.example.dailychoresapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dailychoresapp.data.model.Task
import com.example.dailychoresapp.ui.navigation.Screen
import com.example.dailychoresapp.viewmodel.TaskViewModel

@Composable
fun HomeScreen(navController: NavController, taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.allTasks.observeAsState(initial = emptyList())

    // State to track the task to be deleted
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddTask.route) }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(
                text = "Daily Chores",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                items(tasks) { task ->
                    TaskItem(task, navController, onDeleteRequest = {
                        taskToDelete = task  // Show delete confirmation dialog
                    })
                }
            }
        }
    }

    // Show delete confirmation dialog if a task is selected for deletion
    if (taskToDelete != null) {
        DeleteConfirmationDialog(
            task = taskToDelete!!,
            onConfirmDelete = {
                taskViewModel.deleteTask(taskToDelete!!)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }
}

@Composable
fun TaskItem(task: Task, navController: NavController, onDeleteRequest: () -> Unit) {
    var isChecked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate(Screen.EditTask.passTaskId(task.id)) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it }
                )

                IconButton(onClick = { navController.navigate(Screen.EditTask.passTaskId(task.id)) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Task")
                }

                IconButton(onClick = { onDeleteRequest() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(task: Task, onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Task") },
        text = { Text("Are you sure you want to delete \"${task.title}\"? This action cannot be undone.") },
        confirmButton = {
            Button(onClick = onConfirmDelete) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
