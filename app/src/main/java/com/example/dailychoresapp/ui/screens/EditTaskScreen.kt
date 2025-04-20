package com.example.dailychoresapp.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dailychoresapp.viewmodel.TaskViewModel
import com.example.dailychoresapp.viewmodel.TaskViewModelFactory
import com.example.dailychoresapp.repository.TaskRepository
import androidx.compose.runtime.livedata.observeAsState
import com.example.dailychoresapp.data.database.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavController,
    taskId: Int
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val database = AppDatabase.getDatabase(application)
    val repository = TaskRepository(database.taskDao())

    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(repository)
    )

    val taskToEdit by taskViewModel.getTaskById(taskId).observeAsState()

    if (taskToEdit == null) {
        Text("Loading...", modifier = Modifier.padding(16.dp))
        return
    }

    var title by remember { mutableStateOf(taskToEdit!!.title) }
    var description by remember { mutableStateOf(taskToEdit!!.description) }
    var priority by remember { mutableStateOf(taskToEdit!!.priority) }
    var category by remember { mutableStateOf(taskToEdit!!.category) }

    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var dueDateInput by remember { mutableStateOf(formatter.format(Date(taskToEdit!!.dueDate))) }
    var dueDateMillis by remember { mutableLongStateOf(taskToEdit!!.dueDate) }

    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("Low", "Medium", "High")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Task Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = dueDateInput,
                onValueChange = {
                    dueDateInput = it
                    try {
                        dueDateMillis = formatter.parse(it)?.time ?: dueDateMillis
                    } catch (e: Exception) {
                        Log.e("EditTaskScreen", "Date parsing error: ${e.message}")
                    }
                },
                label = { Text("Due Date (yyyy-MM-dd)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = priority,
                    onValueChange = {},
                    label = { Text("Priority") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    priorities.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                priority = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val updatedTask = taskToEdit!!.copy(
                        title = title,
                        description = description,
                        priority = priority,
                        category = category,
                        dueDate = dueDateMillis
                    )
                    taskViewModel.updateTask(updatedTask)
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Update Task")
            }
        }
    }
}
