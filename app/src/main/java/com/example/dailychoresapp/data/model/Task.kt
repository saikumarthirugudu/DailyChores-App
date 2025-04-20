package com.example.dailychoresapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: String,
    val priority: String,
    val category: String,
    val isCompleted: Boolean = false,
    val reminderTime: String? = null,
    val hasReminder: Boolean = false,
    val repeatInterval: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)