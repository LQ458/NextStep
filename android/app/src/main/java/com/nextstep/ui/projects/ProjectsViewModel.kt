package com.nextstep.ui.projects

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.data.model.Project
import com.nextstep.data.repository.ProjectRepository
import com.nextstep.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectWithTaskCount(
    val project: Project,
    val taskCount: Int
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<ProjectWithTaskCount>>(emptyList())
    val projects: StateFlow<List<ProjectWithTaskCount>> = _projects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // 获取所有项目
                val projects = projectRepository.getAllProjects()
                
                // 转换为 ProjectWithTaskCount 对象列表
                val projectsWithCount = projects.map { project ->
                    // 由于 taskRepository 可能没有 getTasksCountByProject 方法
                    // 这里暂时使用 0 作为任务计数
                    ProjectWithTaskCount(
                        project = project,
                        taskCount = 0
                    )
                }
                
                _projects.value = projectsWithCount
            } catch (e: Exception) {
                // 处理可能的异常
                Log.e("ProjectsViewModel", "加载项目失败", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createProject(name: String, color: String) {
        viewModelScope.launch {
            // 将颜色字符串转换为整数
            val colorInt = android.graphics.Color.parseColor(color)
            val newProject = Project(
                id = 0, // Room会自动生成ID
                name = name,
                color = colorInt
            )
            projectRepository.insertProject(newProject)
            
            // 重新加载项目列表以获取最新数据
            loadProjects()
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
            // 可以添加删除项目中的任务的逻辑
            taskRepository.deleteTasksByProjectId(projectId)
        }
    }
} 