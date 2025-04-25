package com.nextstep.di

import android.content.Context
import androidx.room.Room
import com.nextstep.data.db.AppDatabase
import com.nextstep.data.db.LabelDao
import com.nextstep.data.db.ProjectDao
import com.nextstep.data.db.TaskDao
import com.nextstep.data.repository.LabelRepository
import com.nextstep.data.repository.ProjectRepository
import com.nextstep.data.repository.TaskRepository
import com.nextstep.data.repository.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }
    
    @Provides
    fun provideLabelDao(database: AppDatabase): LabelDao {
        return database.labelDao()
    }

    @Singleton
    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }
    
    @Singleton
    @Provides
    fun provideProjectRepository(projectDao: ProjectDao): ProjectRepository {
        return ProjectRepository(projectDao)
    }
    
    @Singleton
    @Provides
    fun provideLabelRepository(labelDao: LabelDao): LabelRepository {
        return LabelRepository(labelDao)
    }
} 