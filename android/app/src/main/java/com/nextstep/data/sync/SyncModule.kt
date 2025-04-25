package com.nextstep.data.sync

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {
    
    @Singleton
    @Provides
    fun provideSyncManager(@ApplicationContext context: Context): SyncManager {
        return SyncManager(context)
    }
    
    @Singleton
    @Provides
    fun provideSyncWorkerFactory(syncManager: SyncManager): SyncWorkerFactory {
        return SyncWorkerFactory(syncManager)
    }
} 