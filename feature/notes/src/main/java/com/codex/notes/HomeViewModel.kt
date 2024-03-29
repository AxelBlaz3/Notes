package com.codex.notes

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.data.repository.NotesRepository
import com.codex.domain.DeleteNotesUseCase
import com.codex.domain.SaveNotesUseCase
import com.codex.model.data.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val saveNotesUseCase: SaveNotesUseCase
) : ViewModel() {

    private val _notesViewState: MutableStateFlow<NotesViewState> =
        MutableStateFlow(NotesViewState.List)
    val notesViewState
        get() = _notesViewState

    private val _notesSearchState: MutableStateFlow<NotesSearchState> = MutableStateFlow(
        NotesSearchState()
    )
    val notesSearchState: StateFlow<NotesSearchState>
        get() = _notesSearchState.asStateFlow()

    private val _selectedNoteIds: MutableStateFlow<Set<String>> = MutableStateFlow(setOf())
    val selectedNoteIds: StateFlow<Set<String>>
        get() = _selectedNoteIds

    private val _notesOrderState: MutableStateFlow<NotesOrder> =
        MutableStateFlow(NotesOrder.LastEdited)
    val notesOrderState: StateFlow<NotesOrder>
        get() = _notesOrderState

    val notesUiState: StateFlow<NotesUiState> =
        combine(_notesOrderState, _notesSearchState, notesRepository.getNotes(), ::Triple)
            .map {
                it.third.filter { note ->
                    note.title.lowercase().startsWith(it.second.text.lowercase()) ||
                            note.content.lowercase().contains(it.second.text.lowercase())
                }.sortedWith(compareBy { note ->
                    when (it.first) {
                        is NotesOrder.Name -> note.title
                        is NotesOrder.LastEdited -> -1 * note.lastEditedAt.toEpochMilliseconds()
                        else -> -1 * note.createdAt.toEpochMilliseconds()
                    }
                })
            }
            .map(NotesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NotesUiState.Loading
            )

    fun toggleNotesView() {
        if (_notesViewState.value is NotesViewState.List) {
            _notesViewState.value = NotesViewState.Grid
        } else {
            _notesViewState.value = NotesViewState.List
        }
    }

    fun onNoteSelected(noteId: String) {
        val noteIds = _selectedNoteIds.value.toMutableSet()

        if (noteIds.contains(noteId)) {
            noteIds.remove(noteId)
        } else {
            noteIds.add(noteId)
        }

        _selectedNoteIds.value = noteIds
    }

    fun onCancelNotesSelection() {
        _selectedNoteIds.value = setOf()
    }

    fun sortNotesBy(orderState: NotesOrder) {
        _notesOrderState.value = orderState
    }

    fun onSearch(keyword: String) {
        _notesSearchState.value = _notesSearchState.value.copy(text = keyword)
    }

    fun deleteNotes(noteIds: Set<String>) {
        viewModelScope.launch {
            deleteNotesUseCase(noteIds = noteIds)
        }
    }

    fun saveNotes(notes: List<Note>) {
        viewModelScope.launch {
            saveNotesUseCase(notes = notes)
        }
    }

    fun archiveNotes(noteIds: Set<String>) {
        viewModelScope.launch {
            if (notesUiState.value is NotesUiState.Success) {
                val notes = (notesUiState.value as NotesUiState.Success).notes.filter { note ->
                    noteIds.any { noteId ->
                        noteId == note.id
                    }
                }
                saveNotes(notes = notes.map { note -> note.copy(isArchived = true) })
            }
        }
    }

    fun pinNotes(noteIds: Set<String>) {
        if (notesUiState.value is NotesUiState.Success) {
            val notes = (notesUiState.value as NotesUiState.Success).notes.filter { note ->
                noteIds.any { noteId ->
                    noteId == note.id
                }
            }
            saveNotes(notes = notes.map { note -> note.copy(isPinned = true) })
        }
    }
}

sealed interface NotesUiState {

    object Loading : NotesUiState

    data class Success(
        val notes: List<Note>,
    ) : NotesUiState
}

sealed interface NotesViewState {
    object Grid : NotesViewState
    object List : NotesViewState
}

sealed interface NotesOrder {
    object DateCreated : NotesOrder
    object LastEdited : NotesOrder
    object Name : NotesOrder
}

data class NotesSearchState(
    val text: String = ""
)

enum class NotesActionComplete {
    Archive,
    Delete,
    Pin
}
