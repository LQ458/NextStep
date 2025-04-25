package com.nextstep.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.data.model.Project
import com.nextstep.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val projects: Flow<List<Project>> = projectRepository.allProjectsFlow

    fun addProject(project: Project) {
        viewModelScope.launch {
            projectRepository.insertProject(project)
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            projectRepository.deleteProject(project)
        }
    }

    fun getProject(projectId: Long): Flow<Project?> {
        return projectRepository.allProjectsFlow.map { projects ->
            projects.find { it.id == projectId }
        }
    }
} 