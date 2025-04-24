package com.nextstep.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val completed: Boolean,
    val dueDate: Date?,
    val priority: Priority,
    val tags: List<String>,
    val userId: String,
    val createdAt: Date,
    val updatedAt: Date
)

enum class Priority {
    LOW, MEDIUM, HIGH
} 