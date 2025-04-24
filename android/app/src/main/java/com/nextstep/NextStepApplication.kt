package com.nextstep

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

private const val TAG = "NextStepApplication"

@HiltAndroidApp
class NextStepApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate called")
    }
} 