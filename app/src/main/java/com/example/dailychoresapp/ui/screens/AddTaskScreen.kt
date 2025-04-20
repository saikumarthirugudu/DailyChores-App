package com.example.dailychoresapp.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dailychoresapp.data.model.Task
import com.example.dailychoresapp.ui.utils.formatDate
import com.example.dailychoresapp.viewmodel.TaskViewModel
import java.util.*

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    var hasReminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("") }
    var repeatInterval by remember { mutableStateOf("None") }

    val context = LocalContext.current

    val categoryOptions = listOf("Home", "Work", "Personal", "Other")
    val priorityOptions = listOf("Low", "Medium", "High")
    val repeatOptions = listOf("None", "Daily", "Weekly")

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedRepeat by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isNotBlank() && category.isNotBlank() && priority.isNotBlank()) {
                    val task = Task(
                        title = title,
                        description = description,
                        category = category,
                        priority = priority,
                        dueDate = dueDate,
                        isCompleted = isCompleted,
                        hasReminder = hasReminder,
                        reminderTime = if (hasReminder) reminderTime else null,
                        repeatInterval = if (hasReminder) repeatInterval else null
                    )
                    taskViewModel.insert(task)
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .animateContentSize(), // Smooth resizing for animated visibility
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

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Priority Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPriority,
                onExpandedChange = { expandedPriority = !expandedPriority }
            ) {
                OutlinedTextField(
                    value = priority,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false }
                ) {
                    priorityOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                priority = option
                                expandedPriority = false
                            }
                        )
                    }
                }
            }

            // Due Date Picker
            OutlinedTextField(
                value = dueDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Due Date") },
                trailingIcon = {
                    IconButton(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _: DatePicker, year, month, dayOfMonth ->
                                dueDate = formatDate(year, month, dayOfMonth)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Completed toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Mark as Completed")
                Switch(checked = isCompleted, onCheckedChange = { isCompleted = it })
            }

            // Reminder toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Enable Reminder")
                Switch(checked = hasReminder, onCheckedChange = { hasReminder = it })
            }

            // Animated visibility for reminder section
            AnimatedVisibility(visible = hasReminder) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Reminder Time") },
                        trailingIcon = {
                            IconButton(onClick = {
                                val calendar = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        reminderTime = String.format("%02d:%02d", hour, minute)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Pick Time")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Repeat Interval Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedRepeat,
                        onExpandedChange = { expandedRepeat = !expandedRepeat }
                    ) {
                        OutlinedTextField(
                            value = repeatInterval,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repeat") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRepeat)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRepeat,
                            onDismissRequest = { expandedRepeat = false }
                        ) {
                            repeatOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        repeatInterval = option
                                        expandedRepeat = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}