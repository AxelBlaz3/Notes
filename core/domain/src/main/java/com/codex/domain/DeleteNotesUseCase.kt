package com.codex.domain

import com.codex.data.repository.NotesRepository
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    suspend operator fun invoke(noteIds: Set<String>) {
        notesRepository.deleteNotes(noteIds = noteIds)
    }
}