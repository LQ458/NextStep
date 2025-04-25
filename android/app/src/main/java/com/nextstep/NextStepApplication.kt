package com.nextstep

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.nextstep.data.sync.SyncWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

private const val TAG = "NextStepApplication"

@HiltAndroidApp
class NextStepApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: SyncWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate called")
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
} 