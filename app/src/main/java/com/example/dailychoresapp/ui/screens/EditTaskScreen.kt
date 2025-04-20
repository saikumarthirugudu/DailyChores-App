package com.example.dailychoresapp.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
fun EditTaskScreen(
    navController: NavController,
    taskId: Int,
    taskViewModel: TaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val task = taskViewModel.getTaskById(taskId).observeAsState(initial = null).value

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    var hasReminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("") }
    var repeatInterval by remember { mutableStateOf("None") }

    val categoryOptions = listOf("Home", "Work", "Personal", "Other")
    val priorityOptions = listOf("Low", "Medium", "High")
    val repeatOptions = listOf("None", "Daily", "Weekly")

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedRepeat by remember { mutableStateOf(false) }

    LaunchedEffect(task) {
        task?.let {
            title = it.title
            description = it.description
            category = it.category
            priority = it.priority
            dueDate = it.dueDate
            isCompleted = it.isCompleted
            hasReminder = it.hasReminder
            reminderTime = it.reminderTime ?: ""
            repeatInterval = it.repeatInterval ?: "None"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (title.isNotBlank() && category.isNotBlank() && priority.isNotBlank()) {
                        val updatedTask = Task(
                            id = taskId,
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
                        taskViewModel.updateTask(updatedTask)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(50),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text("✓", color = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(8.dp))
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(8.dp))
            )

            AnimatedDropdownField(
                label = "Category",
                value = category,
                options = categoryOptions,
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it },
                onOptionSelected = { category = it; expandedCategory = false }
            )

            AnimatedDropdownField(
                label = "Priority",
                value = priority,
                options = priorityOptions,
                expanded = expandedPriority,
                onExpandedChange = { expandedPriority = it },
                onOptionSelected = { priority = it; expandedPriority = false }
            )

            // Due Date Picker
            OutlinedTextField(
                value = dueDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Due Date") },
                trailingIcon = {
                    IconButton(onClick = {
                        val calendar = Calendar.getInstance()
                        val datePicker = DatePickerDialog(
                            context,
                            { _: DatePicker, year, month, dayOfMonth ->
                                dueDate = formatDate(year, month, dayOfMonth)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        datePicker.show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Completed toggle
            SwitchRow(label = "Mark as Completed", checked = isCompleted) { isCompleted = it }

            // Reminder toggle
            SwitchRow(label = "Enable Reminder", checked = hasReminder) { hasReminder = it }

            AnimatedVisibility(visible = hasReminder, enter = fadeIn(), exit = fadeOut()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Reminder Time Picker
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Reminder Time") },
                        trailingIcon = {
                            IconButton(onClick = {
                                val calendar = Calendar.getInstance()
                                val timePicker = TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        reminderTime = String.format("%02d:%02d", hour, minute)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                )
                                timePicker.show()
                            }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Pick Time")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    AnimatedDropdownField(
                        label = "Repeat",
                        value = repeatInterval,
                        options = repeatOptions,
                        expanded = expandedRepeat,
                        onExpandedChange = { expandedRepeat = it },
                        onOptionSelected = { repeatInterval = it; expandedRepeat = false }
                    )
                }
            }
        }
    }
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedDropdownField(
    label: String,
    value: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}