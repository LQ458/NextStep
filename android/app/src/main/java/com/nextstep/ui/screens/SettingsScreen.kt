package com.nextstep.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val darkMode = remember { mutableStateOf(false) }
    val notificationsEnabled = remember { mutableStateOf(true) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        LazyColumn {
            item {
                SettingsSection(title = "常规设置")
                
                SwitchSettingItem(
                    title = "深色模式",
                    description = "启用应用深色主题",
                    icon = Icons.Default.DarkMode,
                    checked = darkMode.value,
                    onCheckedChange = { darkMode.value = it }
                )
                
                SwitchSettingItem(
                    title = "通知",
                    description = "启用任务提醒通知",
                    icon = Icons.Default.Notifications,
                    checked = notificationsEnabled.value,
                    onCheckedChange = { notificationsEnabled.value = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            
            item {
                SettingsSection(title = "数据与隐私")
                
                SettingItem(
                    title = "数据备份",
                    description = "备份您的任务和项目数据",
                    icon = Icons.Default.Backup,
                    onClick = { /* Handle backup */ }
                )
                
                SettingItem(
                    title = "数据恢复",
                    description = "从备份恢复数据",
                    icon = Icons.Default.Restore,
                    onClick = { /* Handle restore */ }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            
            item {
                SettingsSection(title = "关于")
                
                SettingItem(
                    title = "应用版本",
                    description = "1.0.0",
                    icon = Icons.Default.Info,
                    onClick = { /* Show version info */ }
                )
                
                SettingItem(
                    title = "隐私政策",
                    description = "了解我们如何保护您的隐私",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { /* Open privacy policy */ }
                )
                
                SettingItem(
                    title = "服务条款",
                    description = "查看应用服务条款",
                    icon = Icons.Default.Article,
                    onClick = { /* Open terms of service */ }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
} 