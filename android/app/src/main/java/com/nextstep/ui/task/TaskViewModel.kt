package com.nextstep.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.data.model.Label
import com.nextstep.data.model.Project
import com.nextstep.data.model.Task
import com.nextstep.data.repository.LabelRepository
import com.nextstep.data.repository.ProjectRepository
import com.nextstep.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "TaskViewModel"

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val labelRepository: LabelRepository
) : ViewModel() {

    init {
        Log.d(TAG, "TaskViewModel initialized")
    }
    
    // 过滤类型
    enum class TaskFilterType {
        ALL,                // 所有任务
        BY_PROJECT,         // 按项目过滤
        BY_LABEL,           // 按标签过滤
        TODAY,              // 今天的任务
        WEEK,               // 本周的任务
        OVERDUE,            // 过期任务
        SEARCH              // 搜索结果
    }
    
    // 当前过滤类型
    private val _currentFilterType = MutableStateFlow(TaskFilterType.ALL)
    val currentFilterType: StateFlow<TaskFilterType> = _currentFilterType
    
    // 当前选中的项目ID
    private val _selectedProjectId = MutableStateFlow<Long?>(null)
    val selectedProjectId: StateFlow<Long?> = _selectedProjectId
    
    // 当前选中的标签
    private val _selectedLabel = MutableStateFlow<String?>(null)
    val selectedLabel: StateFlow<String?> = _selectedLabel
    
    // 当前搜索查询
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    // 项目列表
    val projects: StateFlow<List<Project>> = projectRepository.allProjectsFlow
        .catch { e -> 
            Log.e(TAG, "Error collecting projects", e) 
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // 标签列表
    val labels: StateFlow<List<Label>> = labelRepository.allLabelsFlow
        .catch { e -> 
            Log.e(TAG, "Error collecting labels", e) 
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // 根据当前过滤类型动态更新任务列表
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = combine(
        _currentFilterType,
        _selectedProjectId,
        _selectedLabel,
        _searchQuery
    ) { filterType, projectId, label, query ->
        FilterParams(filterType, projectId, label, query)
    }.flatMapLatest { params ->
        when (params.filterType) {
            TaskFilterType.ALL -> taskRepository.getAllTasksFlow()
            TaskFilterType.BY_PROJECT -> {
                params.projectId?.let { 
                    taskRepository.getTasksForProject(it) 
                } ?: taskRepository.getAllTasksFlow()
            }
            TaskFilterType.BY_LABEL -> {
                params.label?.let { 
                    taskRepository.getTasksByLabelFlow(it) 
                } ?: taskRepository.getAllTasksFlow()
            }
            TaskFilterType.TODAY -> taskRepository.getTasksForToday()
            TaskFilterType.WEEK -> taskRepository.getTasksForWeek()
            TaskFilterType.OVERDUE -> taskRepository.getOverdueTasksFlow()
            TaskFilterType.SEARCH -> {
                if (params.query.isNotBlank()) {
                    taskRepository.searchTasksFlow(params.query)
                } else {
                    taskRepository.getAllTasksFlow()
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // 设置过滤类型
    fun setFilterType(filterType: TaskFilterType) {
        _currentFilterType.value = filterType
    }
    
    // 设置选中的项目
    fun setSelectedProject(projectId: Long?) {
        _selectedProjectId.value = projectId
        if (projectId != null) {
            _currentFilterType.value = TaskFilterType.BY_PROJECT
        }
    }
    
    // 设置选中的标签
    fun setSelectedLabel(label: String?) {
        _selectedLabel.value = label
        if (label != null) {
            _currentFilterType.value = TaskFilterType.BY_LABEL
        }
    }
    
    // 设置搜索查询
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            _currentFilterType.value = TaskFilterType.SEARCH
        }
    }

    fun addTask(title: String, description: String, dueDate: Long? = null, priority: Int = 0, projectId: Long = 0) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                val task = Task(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority,
                    projectId = projectId
                )
                taskRepository.insertTask(task)
                Log.d(TAG, "Task added: $title")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding task", e)
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(task)
                Log.d(TAG, "Task updated: ${task.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task", e)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task)
                Log.d(TAG, "Task deleted: ${task.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task", e)
            }
        }
    }
    
    fun updateTaskCompletionStatus(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskCompletionStatus(taskId, completed)
                Log.d(TAG, "Task completion status updated: $taskId to $completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task completion status", e)
            }
        }
    }
    
    fun updateTaskDueDate(taskId: Long, dueDate: Long?) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskDueDate(taskId, dueDate)
                Log.d(TAG, "Task due date updated: $taskId to $dueDate")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task due date", e)
            }
        }
    }
    
    fun updateTaskPriority(taskId: Long, priority: Int) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskPriority(taskId, priority)
                Log.d(TAG, "Task priority updated: $taskId to $priority")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task priority", e)
            }
        }
    }
    
    // 根据日期获取任务
    suspend fun getTasksByDate(date: LocalDate): List<Task> {
        return try {
            taskRepository.getTasksByDate(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks by date: $date", e)
            emptyList()
        }
    }
    
    // 获取日期范围内的任务数量
    suspend fun getTaskCountInDateRange(startDate: LocalDate, endDate: LocalDate): Map<Long, Int> {
        return try {
            taskRepository.getTaskCountInDateRange(startDate, endDate)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting task count in date range", e)
            emptyMap()
        }
    }
    
    // 过滤参数数据类
    private data class FilterParams(
        val filterType: TaskFilterType,
        val projectId: Long?,
        val label: String?,
        val query: String
    )
    
    fun addProject(name: String, color: Int = 0) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                val project = Project(
                    name = name,
                    color = color
                )
                projectRepository.insertProject(project)
                Log.d(TAG, "Project added: $name")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding project", e)
            }
        }
    }
    
    fun updateProject(project: Project) {
        viewModelScope.launch {
            try {
                projectRepository.updateProject(project)
                Log.d(TAG, "Project updated: ${project.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating project", e)
            }
        }
    }
    
    fun deleteProject(project: Project) {
        viewModelScope.launch {
            try {
                projectRepository.deleteProject(project)
                Log.d(TAG, "Project deleted: ${project.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting project", e)
            }
        }
    }
    
    fun addLabel(name: String, color: Int = 0) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                val label = Label(
                    name = name,
                    color = color
                )
                labelRepository.insertLabel(label)
                Log.d(TAG, "Label added: $name")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding label", e)
            }
        }
    }
    
    fun updateLabel(label: Label) {
        viewModelScope.launch {
            try {
                labelRepository.updateLabel(label)
                Log.d(TAG, "Label updated: ${label.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating label", e)
            }
        }
    }
    
    fun deleteLabel(label: Label) {
        viewModelScope.launch {
            try {
                labelRepository.deleteLabel(label)
                Log.d(TAG, "Label deleted: ${label.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting label", e)
            }
        }
    }
} 