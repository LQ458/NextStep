package com.nextstep.data.repository

import android.util.Log
import com.nextstep.data.db.ProjectDao
import com.nextstep.data.model.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProjectRepository"

@Singleton
class ProjectRepository @Inject constructor(private val projectDao: ProjectDao) {
    
    init {
        Log.d(TAG, "ProjectRepository initialized")
    }
    
    val allProjectsFlow: Flow<List<Project>> = projectDao.getAllProjectsFlow()
        .flowOn(Dispatchers.IO)
        .catch { e -> 
            Log.e(TAG, "Error fetching projects flow", e)
            throw e
        }

    suspend fun getAllProjects(): List<Project> = withContext(Dispatchers.IO) {
        try {
            val projects = projectDao.getAllProjects()
            Log.d(TAG, "Got ${projects.size} projects")
            return@withContext projects
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all projects", e)
            throw e
        }
    }
    
    suspend fun getProjectById(projectId: Long): Project? = withContext(Dispatchers.IO) {
        try {
            val project = projectDao.getProjectById(projectId)
            Log.d(TAG, "Got project: ${project?.name}")
            return@withContext project
        } catch (e: Exception) {
            Log.e(TAG, "Error getting project by id", e)
            throw e
        }
    }

    suspend fun insertProject(project: Project) = withContext(Dispatchers.IO) {
        try {
            val id = projectDao.insertProject(project)
            Log.d(TAG, "Project inserted with id: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting project", e)
            throw e
        }
    }

    suspend fun updateProject(project: Project) = withContext(Dispatchers.IO) {
        try {
            val rows = projectDao.updateProject(project)
            Log.d(TAG, "Updated $rows project(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating project", e)
            throw e
        }
    }

    suspend fun deleteProject(project: Project) = withContext(Dispatchers.IO) {
        try {
            val rows = projectDao.deleteProject(project)
            Log.d(TAG, "Deleted $rows project(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting project", e)
            throw e
        }
    }

    suspend fun deleteProject(projectId: Long) = withContext(Dispatchers.IO) {
        try {
            val rows = projectDao.deleteProjectById(projectId)
            Log.d(TAG, "Deleted project with id $projectId, affected rows: $rows")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting project by id", e)
            throw e
        }
    }
} 