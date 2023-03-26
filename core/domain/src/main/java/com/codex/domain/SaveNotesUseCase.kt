package com.codex.domain

import com.codex.data.repository.NotesRepository
import com.codex.model.data.Note
import kotlinx.datetime.Clock
import javax.inject.Inject

class SaveNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    suspend operator fun invoke(notes: List<Note>) {
        notesRepository.addNote(notes = notes.map { note ->
            note.copy(lastEditedAt = Clock.System.now())
        })
    }
}