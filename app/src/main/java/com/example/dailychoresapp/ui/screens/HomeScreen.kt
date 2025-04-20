package com.example.dailychoresapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dailychoresapp.api.RetrofitInstance
import com.example.dailychoresapp.data.database.AppDatabase
import com.example.dailychoresapp.data.model.Task
import com.example.dailychoresapp.repository.QuoteRepository
import com.example.dailychoresapp.repository.TaskRepository
import com.example.dailychoresapp.ui.navigation.Screen
import com.example.dailychoresapp.viewmodel.QuoteViewModel
import com.example.dailychoresapp.viewmodel.QuoteViewModelFactory
import com.example.dailychoresapp.viewmodel.TaskViewModel
import com.example.dailychoresapp.viewmodel.TaskViewModelFactory

@Composable
fun provideTaskViewModel(): TaskViewModel {
    val context = LocalContext.current.applicationContext
    val taskDao = AppDatabase.getDatabase(context).taskDao()
    val repository = remember { TaskRepository(taskDao) }
    val factory = remember { TaskViewModelFactory(repository) }
    return viewModel(factory = factory)
}

@Composable
fun HomeScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = provideTaskViewModel(),
    quoteViewModel: QuoteViewModel = viewModel(
        factory = QuoteViewModelFactory(QuoteRepository(RetrofitInstance.api))
    )
) {
    val tasks by taskViewModel.allTasks.observeAsState(initial = emptyList())
    val quote by quoteViewModel.quote.collectAsState()
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddTask.route)
            }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Daily Chores",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Daily Quote Section
            quote?.let {
                Text(
                    text = "\"${it.content}\" — ${it.author ?: "Unknown"}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        navController = navController,
                        taskViewModel = taskViewModel,
                        onDeleteRequest = {
                            taskToDelete = task
                        }
                    )
                }
            }
        }
    }

    taskToDelete?.let {
        DeleteConfirmationDialog(
            task = it,
            onConfirmDelete = {
                taskViewModel.deleteTask(it)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    task: Task,
    navController: NavController,
    taskViewModel: TaskViewModel,
    onDeleteRequest: () -> Unit
) {
    val dismissState = rememberDismissState()

    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        LaunchedEffect(Unit) {
            onDeleteRequest()
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.error)
                    .padding(20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colors.onError
                )
            }
        },
        dismissContent = {
            val textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        navController.navigate(Screen.EditTask.passTaskId(task.id))
                    },
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = TextStyle(fontSize = 18.sp, textDecoration = textDecoration)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = TextStyle(fontSize = 14.sp, textDecoration = textDecoration)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = {
                                taskViewModel.updateTask(task.copy(isCompleted = it))
                            }
                        )
                        IconButton(onClick = {
                            navController.navigate(Screen.EditTask.passTaskId(task.id))
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    task: Task,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
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
