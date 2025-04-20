package com.example.dailychoresapp.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dailychoresapp.data.model.Task
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Low") }
    var category by remember { mutableStateOf("Personal") }
    var dueDateText by remember { mutableStateOf("") }
    var dueDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Task") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Due Date Picker
                Button(onClick = {
                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, day: Int ->
                            calendar.set(year, month, day)
                            dueDateMillis = calendar.timeInMillis
                            dueDateText = "${day}/${month + 1}/${year}"
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(if (dueDateText.isEmpty()) "Pick Due Date" else "Due: $dueDateText")
                }

                // Priority Dropdown
                var priorityExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        listOf("Low", "Medium", "High").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Dropdown
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        listOf("Personal", "Work", "Home", "Other").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Save Task Button
                Button(
                    onClick = {
                        if (title.isNotEmpty() && description.isNotEmpty()) {
                            val newTask = Task(
                                title = title,
                                description = description,
                                dueDate = dueDateMillis,
                                priority = priority,
                                category = category
                                // isCompleted and createdAt are default
                            )
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Save Task")
                }
            }
        }
    )
}
