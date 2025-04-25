package com.nextstep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextstep.ui.theme.PrimaryRed
import com.nextstep.ui.theme.SuccessGreen
import com.nextstep.ui.viewmodels.TaskViewModel

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    
    val completedTasks = tasks.count { it.completed }
    val pendingTasks = tasks.count { !it.completed }
    val overdueTasks = tasks.count { 
        it.dueDate != null && it.dueDate < System.currentTimeMillis() && !it.completed
    }
    
    val completionRate = if (tasks.isNotEmpty()) {
        completedTasks.toFloat() / tasks.size
    } else 0f
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "任务统计",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        StatCard(
            title = "总任务数",
            value = tasks.size.toString(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "已完成",
                value = completedTasks.toString(),
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "待完成",
                value = pendingTasks.toString(),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
        
        StatCard(
            title = "已过期",
            value = overdueTasks.toString(),
            color = PrimaryRed,
            modifier = Modifier.fillMaxWidth()
        )
        
        StatCard(
            title = "完成率",
            value = String.format("%.1f%%", completionRate * 100),
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )
        
        // 提示文字
        Text(
            text = "更多统计功能将在后续版本推出，敬请期待！",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
} 