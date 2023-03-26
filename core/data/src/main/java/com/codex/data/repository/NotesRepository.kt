package com.codex.data.repository

import com.codex.model.data.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    /**
     * Inserts a given list of notes.
     */
    suspend fun addNote(notes: List<Note>)

    /**
     * Gets available notes as a stream.
     */
    fun getNotes(): Flow<List<Note>>

    /**
     * Deletes a note with given [noteId], if exists.
     */
    suspend fun deleteNotes(noteIds: Set<String>)

//    /**
//     * Updates an existing note with given [note] matching id, otherwise ignores.
//     */
//    suspend fun updateNote(note: Note)
}