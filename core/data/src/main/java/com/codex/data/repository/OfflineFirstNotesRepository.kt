package com.codex.data.repository

import com.codex.database.dao.NotesDao
import com.codex.database.model.NoteEntity
import com.codex.database.model.asExternalModel
import com.codex.model.data.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstNotesRepository @Inject constructor(
    private val notesDao: NotesDao
) : NotesRepository {
    override suspend fun addNote(notes: List<Note>) {
        notesDao.insertOrReplaceNotes(notes.map { note ->
            NoteEntity(
                id = note.id,
                title = note.title,
                content = note.content,
                type = note.type,
                checklist = note.checklist,
                createdAt = note.createdAt,
                lastEditedAt = note.lastEditedAt,
                isPinned = note.isPinned,
                isArchived = note.isArchived,
                labels = note.labels
            )
        })
    }

    override fun getNotes(): Flow<List<Note>> =
        notesDao.getNoteEntities()
            .map { it.map { noteEntity -> noteEntity.asExternalModel() } }

    override suspend fun deleteNotes(noteIds: Set<String>) {
        notesDao.deleteNotes(ids = noteIds)
    }
}