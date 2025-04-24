package com.nextstep.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val tags: List<String>,
    val userId: String,
    val createdAt: Date,
    val updatedAt: Date
) 