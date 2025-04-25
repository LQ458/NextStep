package com.nextstep.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking

private const val TAG = "SyncWorker"

/**
 * WorkManager工作器，执行后台同步任务
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncManager: SyncManager
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d(TAG, "Starting sync work")
        return try {
            runBlocking {
                syncManager.syncData()
            }
            Log.d(TAG, "Sync work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during sync work", e)
            Result.retry()
        }
    }
} 