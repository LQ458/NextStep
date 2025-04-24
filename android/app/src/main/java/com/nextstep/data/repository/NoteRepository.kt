package com.nextstep.data.repository

import com.nextstep.data.db.NoteDao
import com.nextstep.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) = noteDao.insertNote(note)

    suspend fun update(note: Note) = noteDao.updateNote(note)

    suspend fun delete(note: Note) = noteDao.deleteNote(note)
} 