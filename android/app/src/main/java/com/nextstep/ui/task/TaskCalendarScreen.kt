package com.nextstep.ui.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nextstep.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCalendarScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val currentDate = remember { mutableStateOf(Calendar.getInstance()) }
    val displayTasks by viewModel.tasks.collectAsState()
    
    // 获取当前月的日期
    val daysInMonth = remember(currentDate.value) {
        getDaysInMonth(currentDate.value)
    }
    
    // 当前选中的日期
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    
    // 根据选中日期过滤任务
    val filteredTasks = remember(selectedDate, displayTasks) {
        if (selectedDate == null) {
            displayTasks
        } else {
            displayTasks.filter { task ->
                task.dueDate?.let { dueDate ->
                    val taskDate = Calendar.getInstance().apply {
                        timeInMillis = dueDate
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    
                    val selectedDateStart = selectedDate!!.clone() as Calendar
                    selectedDateStart.set(Calendar.HOUR_OF_DAY, 0)
                    selectedDateStart.set(Calendar.MINUTE, 0)
                    selectedDateStart.set(Calendar.SECOND, 0)
                    selectedDateStart.set(Calendar.MILLISECOND, 0)
                    
                    val selectedDateEnd = selectedDateStart.clone() as Calendar
                    selectedDateEnd.add(Calendar.DAY_OF_MONTH, 1)
                    
                    taskDate.timeInMillis in selectedDateStart.timeInMillis until selectedDateEnd.timeInMillis
                } ?: false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日历视图") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 月份导航
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val prevMonth = currentDate.value.clone() as Calendar
                        prevMonth.add(Calendar.MONTH, -1)
                        currentDate.value = prevMonth
                        selectedDate = null
                    }
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "上个月")
                }
                
                val dateFormat = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
                Text(
                    text = dateFormat.format(currentDate.value.time),
                    style = MaterialTheme.typography.titleLarge
                )
                
                IconButton(
                    onClick = {
                        val nextMonth = currentDate.value.clone() as Calendar
                        nextMonth.add(Calendar.MONTH, 1)
                        currentDate.value = nextMonth
                        selectedDate = null
                    }
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "下个月")
                }
            }
            
            // 星期几标题
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val weekDays = listOf("一", "二", "三", "四", "五", "六", "日")
                for (day in weekDays) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // 日历网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(daysInMonth) { day ->
                    val isSelected = selectedDate?.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH) &&
                            selectedDate?.get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
                            selectedDate?.get(Calendar.YEAR) == day.get(Calendar.YEAR)
                    
                    val isToday = day.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                            day.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                            day.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
                    
                    // 检查是否有任务
                    val hasTask = displayTasks.any { task ->
                        task.dueDate?.let { dueDate ->
                            val taskDate = Calendar.getInstance().apply {
                                timeInMillis = dueDate
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            
                            val dayStart = day.clone() as Calendar
                            dayStart.set(Calendar.HOUR_OF_DAY, 0)
                            dayStart.set(Calendar.MINUTE, 0)
                            dayStart.set(Calendar.SECOND, 0)
                            dayStart.set(Calendar.MILLISECOND, 0)
                            
                            val dayEnd = dayStart.clone() as Calendar
                            dayEnd.add(Calendar.DAY_OF_MONTH, 1)
                            
                            taskDate.timeInMillis in dayStart.timeInMillis until dayEnd.timeInMillis
                        } ?: false
                    }
                    
                    // 当前月的日期颜色
                    val isCurrentMonth = day.get(Calendar.MONTH) == currentDate.value.get(Calendar.MONTH)
                    
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .clickable {
                                selectedDate = if (isSelected) null else day.clone() as Calendar
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = if (hasTask) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = day.get(Calendar.DAY_OF_MONTH).toString(),
                                textAlign = TextAlign.Center,
                                color = when {
                                    !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                    isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            if (hasTask) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .align(Alignment.BottomCenter)
                                        .offset(y = (-4).dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
            
            // 选中日期的任务列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    if (selectedDate != null) {
                        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
                        Text(
                            text = dateFormat.format(selectedDate!!.time) + " 的任务",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Text(
                            text = "所有任务",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                
                if (filteredTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "没有任务",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredTasks) { task ->
                        TaskItemSimple(
                            task = task,
                            onClick = {},
                            onCheckboxClick = { completed ->
                                viewModel.updateTask(task.copy(completed = completed))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemSimple(
    task: Task,
    onClick: () -> Unit,
    onCheckboxClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = onCheckboxClick
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// 辅助函数：获取当前月的所有日期
private fun getDaysInMonth(calendar: Calendar): List<Calendar> {
    val result = mutableListOf<Calendar>()
    
    val firstDayOfMonth = calendar.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    
    // 调整为周一开始
    var dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 2
    if (dayOfWeek < 0) dayOfWeek += 7
    
    val start = firstDayOfMonth.clone() as Calendar
    start.add(Calendar.DAY_OF_MONTH, -dayOfWeek)
    
    // 显示6行日期，总共42天
    repeat(42) {
        val day = start.clone() as Calendar
        result.add(day)
        start.add(Calendar.DAY_OF_MONTH, 1)
    }
    
    return result
} 