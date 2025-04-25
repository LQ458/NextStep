package com.nextstep.data.db

import androidx.room.*
import com.nextstep.data.model.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM labels ORDER BY `order` ASC")
    fun getAllLabelsFlow(): Flow<List<Label>>

    @Query("SELECT * FROM labels ORDER BY `order` ASC")
    suspend fun getAllLabels(): List<Label>

    @Query("SELECT * FROM labels WHERE id = :labelId")
    suspend fun getLabelById(labelId: Long): Label?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: Label): Long

    @Update
    suspend fun updateLabel(label: Label): Int

    @Delete
    suspend fun deleteLabel(label: Label): Int
} 