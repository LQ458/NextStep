package com.nextstep.data.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.nextstep.data.repository.LabelRepository
import com.nextstep.data.repository.ProjectRepository
import com.nextstep.data.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SyncService"

/**
 * 同步服务，用于在后台同步数据
 */
@AndroidEntryPoint
class SyncService : Service() {

    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Inject
    lateinit var projectRepository: ProjectRepository
    
    @Inject
    lateinit var labelRepository: LabelRepository
    
    @Inject
    lateinit var syncManager: SyncManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Sync service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Sync service started")
        
        serviceScope.launch {
            try {
                while (true) {
                    syncManager.syncData()
                    // 每30分钟同步一次
                    delay(30 * 60 * 1000L)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in sync loop", e)
            }
        }
        
        // 如果服务被系统杀死，会重新创建
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "Sync service destroyed")
    }
} 