package com.example.dailychoresapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long, // store as timestamp
    val priority: String, // Low, Medium, High
    val category: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
