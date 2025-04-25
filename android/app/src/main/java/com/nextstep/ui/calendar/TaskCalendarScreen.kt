package com.nextstep.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextstep.data.model.Task
import com.nextstep.ui.components.TaskItem
import com.nextstep.ui.task.TaskViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCalendarScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    navigateToTaskDetail: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDayTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    
    // 获取选中日期的任务
    LaunchedEffect(selectedDate) {
        selectedDayTasks = viewModel.getTasksByDate(selectedDate)
    }
    
    // 获取本月每天的任务数量
    val taskCountMap = remember(currentMonth) {
        mutableStateOf<Map<LocalDate, Int>>(emptyMap())
    }
    
    LaunchedEffect(currentMonth) {
        val startDate = currentMonth.atDay(1)
        val endDate = currentMonth.atEndOfMonth()
        val countMap = viewModel.getTaskCountInDateRange(startDate, endDate)
        
        // 转换日期格式
        val dateMap = countMap.entries.associate { (timestamp, count) ->
            val date = LocalDate.ofEpochDay(timestamp / (24 * 60 * 60 * 1000))
            date to count
        }
        
        taskCountMap.value = dateMap
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日历视图") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            // 月份选择器
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "上个月")
                }
                
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "下个月")
                }
            }
            
            // 星期标题
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val daysOfWeek = DayOfWeek.values()
                for (dayOfWeek in daysOfWeek) {
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 日历网格
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val firstDayOfMonth = currentMonth.atDay(1)
                val lastDayOfMonth = currentMonth.atEndOfMonth()
                
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                val totalDays = lastDayOfMonth.dayOfMonth
                
                val totalCells = firstDayOfWeek + totalDays
                val rows = (totalCells + 6) / 7
                
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (col in 0 until 7) {
                            val index = row * 7 + col
                            val dayOfMonth = index - firstDayOfWeek + 1
                            
                            if (dayOfMonth in 1..totalDays) {
                                val date = currentMonth.atDay(dayOfMonth)
                                val isSelected = date == selectedDate
                                val isToday = date == LocalDate.now()
                                
                                // 获取当天的任务数量
                                val taskCount = taskCountMap.value[date] ?: 0
                                
                                DayCell(
                                    day = dayOfMonth,
                                    isSelected = isSelected,
                                    isToday = isToday,
                                    taskCount = taskCount,
                                    onClick = {
                                        selectedDate = date
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                // 空白单元格
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .weight(1f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // 选中日期的任务列表
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Divider()
                    
                    if (selectedDayTasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "没有任务",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(selectedDayTasks) { task ->
                                TaskItem(
                                    task = task,
                                    onClick = { navigateToTaskDetail(task.id) },
                                    onCheckboxClick = { 
                                        viewModel.updateTaskCompletionStatus(task.id, !task.completed)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    taskCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.toString(),
                color = textColor,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            if (taskCount > 0) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
} 