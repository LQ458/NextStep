package com.nextstep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.nextstep.data.db.AppDatabase
import com.nextstep.data.repository.TaskRepository
import com.nextstep.ui.task.TaskNavigation
import com.nextstep.ui.task.TaskViewModel
import com.nextstep.ui.theme.NextStepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao())
        val viewModel = TaskViewModel(repository)

        setContent {
            NextStepTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskNavigation(viewModel)
                }
            }
        }
    }
} 