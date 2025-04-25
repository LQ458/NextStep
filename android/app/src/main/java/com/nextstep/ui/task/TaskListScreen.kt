package com.nextstep.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nextstep.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    projectId: Long = 0,
    onAddTask: () -> Unit,
    onOpenCalendar: () -> Unit = {},
    onOpenProjects: () -> Unit = {},
    onOpenSearch: () -> Unit = {}
) {
    val tasks by viewModel.tasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val currentFilterType by viewModel.currentFilterType.collectAsState()
    val selectedProjectId by viewModel.selectedProjectId.collectAsState()
    
    // 如果提供了projectId参数，设置当前项目
    LaunchedEffect(projectId) {
        if (projectId > 0) {
            viewModel.setSelectedProject(projectId)
        }
    }
    
    // 获取当前项目名称
    val currentProjectName = remember(selectedProjectId, projects) {
        selectedProjectId?.let { id ->
            projects.find { it.id == id }?.name ?: "任务"
        } ?: "所有任务"
    }
    
    // 获取筛选器标题
    val screenTitle = remember(currentFilterType, currentProjectName) {
        when (currentFilterType) {
            TaskViewModel.TaskFilterType.ALL -> "所有任务"
            TaskViewModel.TaskFilterType.BY_PROJECT -> currentProjectName
            TaskViewModel.TaskFilterType.BY_LABEL -> "标签筛选"
            TaskViewModel.TaskFilterType.TODAY -> "今天"
            TaskViewModel.TaskFilterType.WEEK -> "本周"
            TaskViewModel.TaskFilterType.OVERDUE -> "已过期"
            TaskViewModel.TaskFilterType.SEARCH -> "搜索结果"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                actions = {
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = onOpenCalendar) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "日历视图")
                    }
                    IconButton(onClick = onOpenProjects) {
                        Icon(Icons.Default.Folder, contentDescription = "项目")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "添加任务")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentFilterType == TaskViewModel.TaskFilterType.ALL,
                    onClick = { viewModel.setFilterType(TaskViewModel.TaskFilterType.ALL) },
                    icon = { Icon(Icons.Default.List, contentDescription = "所有") },
                    label = { Text("所有") }
                )
                NavigationBarItem(
                    selected = currentFilterType == TaskViewModel.TaskFilterType.TODAY,
                    onClick = { viewModel.setFilterType(TaskViewModel.TaskFilterType.TODAY) },
                    icon = { Icon(Icons.Default.Today, contentDescription = "今天") },
                    label = { Text("今天") }
                )
                NavigationBarItem(
                    selected = currentFilterType == TaskViewModel.TaskFilterType.WEEK,
                    onClick = { viewModel.setFilterType(TaskViewModel.TaskFilterType.WEEK) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "本周") },
                    label = { Text("本周") }
                )
                NavigationBarItem(
                    selected = currentFilterType == TaskViewModel.TaskFilterType.OVERDUE,
                    onClick = { viewModel.setFilterType(TaskViewModel.TaskFilterType.OVERDUE) },
                    icon = { Icon(Icons.Default.Warning, contentDescription = "已过期") },
                    label = { Text("过期") }
                )
            }
        }
    ) { padding ->
        if (tasks.isEmpty()) {
            // 显示空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "没有任务",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "点击 + 按钮添加新任务",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = { viewModel.updateTask(task.copy(completed = !task.completed)) },
                        onDeleteClick = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 优先级指示器
            if (task.priority > 0) {
                val priorityColor = when (task.priority) {
                    1 -> Color.Blue
                    2 -> Color.Green
                    3 -> Color.Red
                    else -> Color.Gray
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 4.dp)
                        .padding(top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.Center)
                            .padding(1.dp)
                            .background(priorityColor, CircleShape)
                    )
                }
            }
            
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onTaskClick() }
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.completed) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                if (task.description != null && task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // 显示截止日期
                task.dueDate?.let { dueDate ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = Date(dueDate)
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                    
                    val isOverdue = date.before(today) && !task.completed
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dateFormat.format(date),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除任务"
                )
            }
        }
    }
} 