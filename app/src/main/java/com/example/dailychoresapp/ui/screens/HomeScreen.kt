package com.example.dailychoresapp.ui.screens

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenu
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.FloatingActionButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.LinearProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.primarySurface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dailychoresapp.data.model.Task
import com.example.dailychoresapp.ui.components.TaskItem
import com.example.dailychoresapp.ui.navigation.Screen
import com.example.dailychoresapp.viewmodel.QuoteViewModel
import com.example.dailychoresapp.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    quoteViewModel: QuoteViewModel
) {
    val allTasks by taskViewModel.incompleteTasks.observeAsState(emptyList())
    val quote by quoteViewModel.quote.collectAsState()
    val isLoading by quoteViewModel.isLoading.collectAsState()
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedPriority by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSortOption by remember { mutableStateOf("None") }
    var showMenu by remember { mutableStateOf(false) }

    val filteredTasks = allTasks
        .filter { it.title.contains(searchQuery.text, true) || it.description.contains(searchQuery.text, true) }
        .filter { if (selectedPriority != "All") it.priority == selectedPriority else true }
        .filter { if (selectedCategory != "All") it.category == selectedCategory else true }
        .sortedWith(compareBy(
            when (selectedSortOption) {
                "Due Date" -> { task: Task -> task.dueDate }
                "Priority" -> { task: Task -> task.priority }
                else -> { task: Task -> task.id.toString() }
            }
        ))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Chores") },
                navigationIcon = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                backgroundColor = MaterialTheme.colors.primarySurface,
                elevation = 8.dp
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddTask.route)
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Quote Section
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                quote?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🌟 Today's Motivation",
                                    style = MaterialTheme.typography.subtitle1.copy(fontSize = 16.sp)
                                )
                                IconButton(onClick = { quoteViewModel.fetchDailyQuote() }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh Quote")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "“${it.q}”",
                                style = MaterialTheme.typography.h6.copy(fontSize = 18.sp),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "- ${it.a}",
                                style = MaterialTheme.typography.caption.copy(fontSize = 14.sp),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                label = { Text("Search Tasks") }
            )

            // Filter Row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                DropdownMenuWithLabel("Priority", listOf("All", "Low", "Medium", "High"), selectedPriority) { selectedPriority = it }
                DropdownMenuWithLabel("Category", listOf("All", "Work", "Personal", "Others"), selectedCategory) { selectedCategory = it }
                DropdownMenuWithLabel("Sort", listOf("None", "Due Date", "Priority"), selectedSortOption) { selectedSortOption = it }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task List
            LazyColumn {
                items(filteredTasks, key = { it.id }) { task ->
                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        LaunchedEffect(task.id) {
                            taskToDelete = task
                            dismissState.reset()
                        }
                    }

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                            }
                        },
                        dismissContent = {
                            TaskItem(
                                task = task,
                                onCheckedChange = {
                                    taskViewModel.updateTask(task.copy(isCompleted = it))
                                },
                                onEditClick = {
                                    navController.navigate(Screen.EditTask.passTaskId(task.id))
                                },
                                onDeleteClick = {
                                    taskToDelete = task
                                },
                                onTaskClick = {
                                    navController.navigate(Screen.TaskDetailScreen.passTaskId(task.id))
                                }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Logout Menu
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
                showMenu = false
            }) {
                Text("Logout")
            }
        }
    }

    // Delete Confirmation Dialog
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    taskViewModel.deleteTask(task)
                    taskToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DropdownMenuWithLabel(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.padding(4.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.caption)
            OutlinedButton(onClick = { expanded = true }) {
                Text(selected)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(onClick = {
                    onSelected(it)
                    expanded = false
                }) {
                    Text(it)
                }
            }
        }
    }
}