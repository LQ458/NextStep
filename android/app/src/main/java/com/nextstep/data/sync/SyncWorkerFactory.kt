package com.nextstep.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 自定义WorkerFactory，用于创建具有依赖注入的SyncWorker
 */
@Singleton
class SyncWorkerFactory @Inject constructor(
    private val syncManager: SyncManager
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> {
                SyncWorker(appContext, workerParameters, syncManager)
            }
            else -> null
        }
    }
} 