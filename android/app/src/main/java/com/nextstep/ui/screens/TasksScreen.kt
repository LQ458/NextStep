package com.nextstep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextstep.data.model.Task
import com.nextstep.ui.components.TaskItem
import com.nextstep.ui.viewmodels.TaskFilter
import com.nextstep.ui.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.filteredTasks.collectAsState(initial = emptyList())
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar
            TextField(
                value = "",
                onValueChange = { /* TODO: Implement search */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("搜索任务...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                singleLine = true
            )
            
            // Filter tabs
            ScrollableTabRow(
                selectedTabIndex = selectedFilter.ordinal,
                edgePadding = 16.dp
            ) {
                TaskFilter.values().forEachIndexed { index, filter ->
                    Tab(
                        selected = selectedFilter.ordinal == index,
                        onClick = { viewModel.setFilter(filter) },
                        text = {
                            Text(
                                when (filter) {
                                    TaskFilter.ALL -> "全部"
                                    TaskFilter.TODAY -> "今日"
                                    TaskFilter.UPCOMING -> "即将到期"
                                    TaskFilter.COMPLETED -> "已完成"
                                    TaskFilter.OVERDUE -> "已过期"
                                }
                            )
                        }
                    )
                }
            }
            
            // Task list
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "暂无任务",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 0.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onCheckboxClick = { viewModel.toggleTaskCompletion(task) }
                        )
                    }
                    
                    // Add space at the bottom for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
        
        // FAB for adding new task
        FloatingActionButton(
            onClick = onAddTaskClick,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Task",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
} 