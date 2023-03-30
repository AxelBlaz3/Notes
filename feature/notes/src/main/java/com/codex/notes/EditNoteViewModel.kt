package com.codex.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.common.decoder.StringDecoder
import com.codex.data.repository.NotesRepository
import com.codex.domain.DeleteNotesUseCase
import com.codex.domain.SaveNotesUseCase
import com.codex.model.NoteContentType
import com.codex.model.data.Note
import com.codex.notes.navigation.EditNoteArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val saveNotesUseCase: SaveNotesUseCase,
    stringDecoder: StringDecoder,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val editNoteArgs: EditNoteArgs = EditNoteArgs(savedStateHandle, stringDecoder)

    val noteId: String = editNoteArgs.noteId

    private var _currentNote: MutableStateFlow<EditNotesUiState> =
        MutableStateFlow(EditNotesUiState())
    val currentNote: StateFlow<EditNotesUiState> = _currentNote

    init {
        if (noteId.isNotEmpty()) {
            viewModelScope.launch {
                notesRepository.getNotes().collectLatest { notes ->
                    _currentNote.value = _currentNote.value.copy(note = notes.first { note ->
                        note.id == noteId
                    })
                }
            }
        }
    }

    fun onCurrentNoteChange(editNotesUiState: EditNotesUiState) {
        _currentNote.value = editNotesUiState
    }

    fun onCheckboxItemToggle(key: String) {
        val mutableChecklist = _currentNote.value.note.checklist.toMutableMap()

        mutableChecklist.apply {
            put(key, !_currentNote.value.note.checklist.getOrDefault(key, false))
        }

        _currentNote.value =
            _currentNote.value.copy(note = _currentNote.value.note.copy(checklist = mutableChecklist))
    }

    fun onCheckboxItemTextChange(oldKey: String, newKey: String) {
        val note = _currentNote.value.note

        val mutableCheckList = note.checklist.toMutableMap()

        mutableCheckList.apply {
            val oldValue = remove(oldKey)
            put(newKey, oldValue ?: false)
        }

        _currentNote.value = _currentNote.value.copy(note = note.copy(checklist = mutableCheckList))
    }

    fun addCheckListItem(key: String, value: Boolean) {
        val note = _currentNote.value.note

        val mutableChecklist = note.checklist.toMutableMap()

        mutableChecklist.apply {
            put(key, value)

            // Remove the dummy key. This is done so as a workaround when the user taps on "Add list item".
            if (key.isNotEmpty()) {
                // You don't want to remove the empty key if that's what you want to add.
                remove("")
            }
        }

        _currentNote.value = _currentNote.value.copy(note = note.copy(checklist = mutableChecklist))
    }

    fun deleteChecklistItem(key: String) {
        val mutableChecklist = _currentNote.value.note.checklist.toMutableMap()

        mutableChecklist.remove(key)
        _currentNote.value =
            _currentNote.value.copy(note = _currentNote.value.note.copy(checklist = mutableChecklist))
    }

    fun saveCurrentNote() {
        viewModelScope.launch {
            saveNotesUseCase(listOf(_currentNote.value.note))
            _currentNote.value = EditNotesUiState()
        }
    }

    fun onDeleteNote(noteId: String) {
        viewModelScope.launch {
            deleteNotesUseCase(setOf(noteId))
        }
    }
}

data class EditNotesUiState(
    val note: Note = Note(
        id = UUID.randomUUID().toString(),
        title = "",
        content = "",
        type = NoteContentType.SimpleText,
        checklist = mapOf("" to false),
        createdAt = Clock.System.now(),
        lastEditedAt = Clock.System.now(),
        isPinned = false,
        isArchived = false,
        labels = arrayListOf()
    )
)