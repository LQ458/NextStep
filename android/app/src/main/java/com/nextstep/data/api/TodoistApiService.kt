package com.nextstep.data.api

import com.nextstep.data.model.Label
import com.nextstep.data.model.Project
import com.nextstep.data.model.Task
import retrofit2.Response
import retrofit2.http.*

/**
 * Todoist API服务接口
 */
interface TodoistApiService {

    // 任务相关API
    @GET("tasks")
    suspend fun getTasks(): Response<List<Task>>
    
    @POST("tasks")
    suspend fun createTask(@Body task: TaskRequest): Response<Task>
    
    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body task: TaskRequest): Response<Task>
    
    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long): Response<Unit>
    
    @POST("tasks/{id}/close")
    suspend fun completeTask(@Path("id") id: Long): Response<Unit>
    
    @POST("tasks/{id}/reopen")
    suspend fun reopenTask(@Path("id") id: Long): Response<Unit>
    
    // 项目相关API
    @GET("projects")
    suspend fun getProjects(): Response<List<Project>>
    
    @POST("projects")
    suspend fun createProject(@Body project: ProjectRequest): Response<Project>
    
    @PUT("projects/{id}")
    suspend fun updateProject(@Path("id") id: Long, @Body project: ProjectRequest): Response<Project>
    
    @DELETE("projects/{id}")
    suspend fun deleteProject(@Path("id") id: Long): Response<Unit>
    
    // 标签相关API
    @GET("labels")
    suspend fun getLabels(): Response<List<Label>>
    
    @POST("labels")
    suspend fun createLabel(@Body label: LabelRequest): Response<Label>
    
    @PUT("labels/{id}")
    suspend fun updateLabel(@Path("id") id: Long, @Body label: LabelRequest): Response<Label>
    
    @DELETE("labels/{id}")
    suspend fun deleteLabel(@Path("id") id: Long): Response<Unit>
    
    // 同步API
    @POST("sync")
    suspend fun sync(@Body syncRequest: SyncRequest): Response<SyncResponse>
}

/**
 * API请求/响应数据类
 */
data class TaskRequest(
    val content: String,
    val description: String = "",
    val project_id: Long? = null,
    val due_date: String? = null,
    val priority: Int = 1,
    val label_ids: List<Long> = emptyList()
)

data class ProjectRequest(
    val name: String,
    val color: Int = 0
)

data class LabelRequest(
    val name: String,
    val color: Int = 0
)

data class SyncRequest(
    val sync_token: String? = null,
    val resource_types: List<String> = listOf("all")
)

data class SyncResponse(
    val sync_token: String,
    val full_sync: Boolean,
    val tasks: List<Task>? = null,
    val projects: List<Project>? = null,
    val labels: List<Label>? = null
) 