package com.nextstep.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        androidx.room.Index("project_id")
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String,
    
    val description: String? = null,
    
    val completed: Boolean = false,
    
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    
    val priority: Int = 0, // 0: None, 1: Low, 2: Medium, 3: High
    
    @ColumnInfo(name = "project_id")
    val projectId: Long? = null,
    
    @ColumnInfo(name = "labels")
    val labels: String? = null, // Comma separated labels
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) 