package com.nextstep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextstep.data.model.Task
import com.nextstep.ui.components.TaskItem
import com.nextstep.ui.theme.NeutralGray
import com.nextstep.ui.theme.PrimaryRed
import com.nextstep.ui.viewmodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

@Composable
fun TaskCalendarScreen(
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    val tasks by viewModel.tasksForSelectedDate(selectedDate).collectAsState(initial = emptyList())
    val monthlyTaskCount by viewModel.getMonthlyTaskCount(selectedDate).collectAsState(initial = emptyMap())
    
    Column(modifier = modifier.fillMaxSize()) {
        CalendarHeader(
            selectedDate = selectedDate,
            onPreviousClick = {
                calendar.timeInMillis = selectedDate
                calendar.add(Calendar.MONTH, -1)
                selectedDate = calendar.timeInMillis
            },
            onNextClick = {
                calendar.timeInMillis = selectedDate
                calendar.add(Calendar.MONTH, 1)
                selectedDate = calendar.timeInMillis
            }
        )
        
        MonthCalendar(
            selectedDate = selectedDate,
            onDateSelected = { 
                selectedDate = it
            },
            taskCount = monthlyTaskCount
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        DayTaskList(
            tasks = tasks,
            onTaskClick = onTaskClick
        )
    }
}

@Composable
fun CalendarHeader(
    selectedDate: Long,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_previous),
                contentDescription = "Previous Month"
            )
        }
        
        Text(
            text = dateFormat.format(Date(selectedDate)),
            style = MaterialTheme.typography.titleLarge
        )
        
        IconButton(onClick = onNextClick) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_next),
                contentDescription = "Next Month"
            )
        }
    }
}

@Composable
fun MonthCalendar(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    taskCount: Map<Long, Int>
) {
    val calendar = remember { Calendar.getInstance() }
    calendar.timeInMillis = selectedDate
    
    // Save current day
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    
    // Move to first day of month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    // Get day of week for first day (0 = Sunday, 1 = Monday, etc.)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    
    // Calculate days in month
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // Days of week header
    val daysOfWeek = listOf("日", "一", "二", "三", "四", "五", "六")
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            for (day in daysOfWeek) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
        
        // Calendar grid
        for (week in 0 until 6) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val dayOfMonth = index - firstDayOfWeek + 1
                    
                    if (dayOfMonth in 1..daysInMonth) {
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val dayMillis = calendar.timeInMillis
                        
                        // Normalize to start of day for comparison
                        val normalizedDayMillis = (dayMillis / 86400000) * 86400000
                        val hasTask = taskCount[normalizedDayMillis] ?: 0 > 0
                        val isSelected = dayOfMonth == SimpleDateFormat("d", Locale.getDefault()).format(Date(selectedDate)).toInt()
                        
                        CalendarDay(
                            day = dayOfMonth.toString(),
                            isSelected = isSelected,
                            hasTask = hasTask,
                            onClick = {
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                onDateSelected(calendar.timeInMillis)
                            }
                        )
                    } else {
                        // Empty space for days not in this month
                        Box(modifier = Modifier.weight(1f)) {
                            Spacer(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.CalendarDay(
    day: String,
    isSelected: Boolean,
    hasTask: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .weight(1f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (isSelected) PrimaryRed else Color.Transparent)
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color.White else Color.Black
            )
            if (hasTask) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else PrimaryRed)
                )
            }
        }
    }
}

@Composable
fun DayTaskList(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = if (tasks.isEmpty()) "今日无任务" else "今日任务 (${tasks.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // Add bottom padding for FAB
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onClick = { onTaskClick(task) }
                )
            }
        }
    }
} 