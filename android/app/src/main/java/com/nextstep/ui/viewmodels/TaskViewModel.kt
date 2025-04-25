package com.nextstep.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.data.model.Project
import com.nextstep.data.model.Task
import com.nextstep.data.repository.ProjectRepository
import com.nextstep.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val allTasks = taskRepository.getAllTasksFlow()
    val allProjects = projectRepository.allProjectsFlow
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()
    
    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject = _selectedProject.asStateFlow()
    
    val filteredTasks = combine(
        allTasks,
        _selectedFilter,
        _selectedProject,
        _searchQuery
    ) { tasks, filter, project, query ->
        var result = tasks
        
        // Apply project filter
        if (project != null) {
            result = result.filter { it.projectId == project.id }
        }
        
        // Apply task filter
        result = when (filter) {
            TaskFilter.ALL -> result
            TaskFilter.TODAY -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis
                
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endOfDay = calendar.timeInMillis
                
                result.filter { it.dueDate in startOfDay..endOfDay }
            }
            TaskFilter.UPCOMING -> {
                val currentTime = System.currentTimeMillis()
                result.filter { 
                    it.dueDate != null && it.dueDate > currentTime && !it.completed
                }
            }
            TaskFilter.COMPLETED -> result.filter { it.completed }
            TaskFilter.OVERDUE -> {
                val currentTime = System.currentTimeMillis()
                result.filter {
                    it.dueDate != null && it.dueDate < currentTime && !it.completed
                }
            }
        }
        
        // Apply search query
        if (query.isNotBlank()) {
            result = result.filter {
                it.title.contains(query, ignoreCase = true) || 
                (it.description?.contains(query, ignoreCase = true) ?: false)
            }
        }
        
        result
    }
    
    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
        _selectedProject.value = null
    }
    
    fun selectProject(project: Project?) {
        _selectedProject.value = project
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun tasksForSelectedDate(date: Long): Flow<List<Task>> {
        // Normalize date to start of day
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis
        
        return taskRepository.getTasksForPeriod(startOfDay, endOfDay)
    }
    
    private fun getTasksForPeriodFlow(startOfDay: Long, endOfDay: Long): Flow<List<Task>> {
        return taskRepository.getTasksForPeriod(startOfDay, endOfDay)
    }
    
    fun getMonthlyTaskCount(date: Long): Flow<Map<Long, Int>> = flow {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        
        // Set to first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        // Set to last day of month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis
        
        val taskCounts = taskRepository.getTaskCountInDateRange(startOfMonth, endOfMonth)
        emit(taskCounts)
    }
    
    fun addTask(task: Task) = viewModelScope.launch {
        taskRepository.insertTask(task)
    }
    
    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.updateTask(task)
    }
    
    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.deleteTask(task)
    }
    
    fun toggleTaskCompletion(task: Task) = viewModelScope.launch {
        taskRepository.updateTaskCompletionStatus(task.id, !task.completed)
    }
    
    fun addProject(project: Project) = viewModelScope.launch {
        projectRepository.insertProject(project)
    }
    
    fun updateProject(project: Project) = viewModelScope.launch {
        projectRepository.updateProject(project)
    }
    
    fun deleteProject(project: Project) = viewModelScope.launch {
        projectRepository.deleteProject(project)
    }
}

enum class TaskFilter {
    ALL, TODAY, UPCOMING, COMPLETED, OVERDUE
} 