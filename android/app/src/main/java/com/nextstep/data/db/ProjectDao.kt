package com.nextstep.data.db

import androidx.room.*
import com.nextstep.data.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY id ASC")
    fun getAllProjectsFlow(): Flow<List<Project>>

    @Query("SELECT * FROM projects ORDER BY id ASC")
    suspend fun getAllProjects(): List<Project>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: Long): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project): Int

    @Delete
    suspend fun deleteProject(project: Project): Int

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long): Int
} 