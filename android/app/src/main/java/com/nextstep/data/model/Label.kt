package com.nextstep.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels")
data class Label(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: Int = 0,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: Int = 0
) 