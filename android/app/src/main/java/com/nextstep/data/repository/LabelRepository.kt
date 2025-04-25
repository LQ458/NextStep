package com.nextstep.data.repository

import android.util.Log
import com.nextstep.data.db.LabelDao
import com.nextstep.data.model.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LabelRepository"

class LabelRepository @Inject constructor(private val labelDao: LabelDao) {
    
    init {
        Log.d(TAG, "LabelRepository initialized")
    }
    
    val allLabelsFlow: Flow<List<Label>> = labelDao.getAllLabelsFlow()
        .flowOn(Dispatchers.IO)
        .catch { e -> 
            Log.e(TAG, "Error fetching labels flow", e)
            throw e
        }

    suspend fun getAllLabels(): List<Label> = withContext(Dispatchers.IO) {
        try {
            val labels = labelDao.getAllLabels()
            Log.d(TAG, "Got ${labels.size} labels")
            return@withContext labels
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all labels", e)
            throw e
        }
    }
    
    suspend fun getLabelById(labelId: Long): Label? = withContext(Dispatchers.IO) {
        try {
            val label = labelDao.getLabelById(labelId)
            Log.d(TAG, "Got label: ${label?.name}")
            return@withContext label
        } catch (e: Exception) {
            Log.e(TAG, "Error getting label by id", e)
            throw e
        }
    }

    suspend fun insertLabel(label: Label) = withContext(Dispatchers.IO) {
        try {
            val id = labelDao.insertLabel(label)
            Log.d(TAG, "Label inserted with id: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting label", e)
            throw e
        }
    }

    suspend fun updateLabel(label: Label) = withContext(Dispatchers.IO) {
        try {
            val rows = labelDao.updateLabel(label)
            Log.d(TAG, "Updated $rows label(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating label", e)
            throw e
        }
    }

    suspend fun deleteLabel(label: Label) = withContext(Dispatchers.IO) {
        try {
            val rows = labelDao.deleteLabel(label)
            Log.d(TAG, "Deleted $rows label(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting label", e)
            throw e
        }
    }
} 