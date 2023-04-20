package com.codex.notes

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codex.designsystem.component.NotesAppBar
import com.codex.designsystem.component.NotesBottomAppBar
import com.codex.model.NoteContentType
import com.codex.model.data.Note
import com.codex.notes.provider.NotesFileProvider
import com.google.mlkit.vision.common.InputImage
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun EditNotesRoute(
    modifier: Modifier = Modifier,
    viewModel: EditNoteViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val editNotesUiState: EditNotesUiState by viewModel.currentNote
        .collectAsStateWithLifecycle()

    EditNoteScreen(
        modifier = modifier,
        editNotesUiState = editNotesUiState,
        onBackClick = onBackClick,
        onNoteChange = viewModel::onCurrentNoteChange,
        onPinClick = viewModel::onCurrentNoteChange,
        onArchiveClick = viewModel::onCurrentNoteChange,
        onSaveClick = {
            viewModel.saveCurrentNote()
            onBackClick()
        },
        onCheckboxClick = viewModel::onCurrentNoteChange,
        onCheckboxItemToggle = viewModel::onCheckboxItemToggle,
        onCheckboxItemTextChange = viewModel::onCheckboxItemTextChange,
        onAddListItem = viewModel::addCheckListItem,
        onDeleteChecklistItem = viewModel::deleteChecklistItem,
        onDeleteNote = viewModel::onDeleteNote,
        isNewNote = viewModel.noteId.isEmpty(),
        onImageCapture = viewModel::processImage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    editNotesUiState: EditNotesUiState,
    onNoteChange: (EditNotesUiState) -> Unit,
    onCheckboxClick: (EditNotesUiState) -> Unit,
    onCheckboxItemToggle: (String) -> Unit,
    onCheckboxItemTextChange: (String, String) -> Unit,
    onPinClick: (EditNotesUiState) -> Unit,
    onArchiveClick: (EditNotesUiState) -> Unit,
    onSaveClick: (EditNotesUiState) -> Unit,
    onAddListItem: (String, Boolean) -> Unit,
    onDeleteChecklistItem: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onImageCapture: (InputImage) -> Unit,
    isNewNote: Boolean
) {

    val currentNote: Note = editNotesUiState.note
    val context = LocalContext.current

    var uri: Uri? = remember { null }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        println("Uri: $it")
        onImageCapture(InputImage.fromFilePath(context, uri!!))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        NotesAppBar(title = {}, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
            }
        }, actions = {
        })

        OutlinedTextField(
            value = currentNote.title, onValueChange = { title ->
                onNoteChange(editNotesUiState.copy(note = currentNote.copy(title = title)))
            },
            modifier = modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = MaterialTheme.colorScheme.background
            ),
            textStyle = MaterialTheme.typography.titleLarge,
            placeholder = {
                Text("Title", style = MaterialTheme.typography.titleLarge)
            }
        )

        when (currentNote.type) {
            NoteContentType.Checkboxes -> {
                val focusRequester = remember {
                    FocusRequester()
                }

                Column(
                    modifier
                        .verticalScroll(state = rememberScrollState())
                        .weight(1f)
                ) {
                    if (currentNote.checklist.isEmpty()) {
                        onAddListItem("", false)
                    }

                    currentNote.checklist.forEach { checkListItem ->
                        CheckboxItem(
                            checkListItem = checkListItem,
                            onCheckboxItemToggle = onCheckboxItemToggle,
                            onValueChange = { newValue ->
                                onCheckboxItemTextChange(checkListItem.key, newValue)
                            },
                            focusRequester = focusRequester,
                            shouldFocus = checkListItem.key.isEmpty(),
                            onDeleteChecklistItem = onDeleteChecklistItem
                        )
                    }

                    TextButton(onClick = {
                        onAddListItem("", false)
                    }) {
                        Icon(
                            Icons.Outlined.Add,
                            null,
                            modifier = modifier.padding(
                                end = ButtonDefaults.IconSpacing,
                                start = ButtonDefaults.IconSpacing + 8.dp
                            )
                        )
                        Text(text = "List item", modifier = modifier.fillMaxWidth())
                    }
                }
            }

            else -> {
                OutlinedTextField(
                    value = currentNote.content,
                    onValueChange = { content ->
                        onNoteChange(editNotesUiState.copy(note = currentNote.copy(content = content)))
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = MaterialTheme.colorScheme.background
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                    ),
                    placeholder = {
                        Text("Note")
                    })
            }
        }

        Text(
            text = stringResource(
                id = R.string.last_edited, currentNote.lastEditedAt.toLocalDateTime(
                    TimeZone.currentSystemDefault()
                ).toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("MMM d, h:m a"))
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(8.dp)
        )

        NotesBottomAppBar(
            modifier = modifier.imePadding(),
            actions = {
                IconButton(onClick = {
                    onCheckboxClick(
                        editNotesUiState.copy(
                            note = currentNote.copy(
                                type = if (currentNote.type == NoteContentType.Checkboxes) NoteContentType.SimpleText else {
                                    NoteContentType.Checkboxes
                                }
                            )
                        )
                    )
                }) {
                    Icon(
                        if (currentNote.type != NoteContentType.Checkboxes) {
                            Icons.Outlined.CheckBox
                        } else {
                            Icons.Outlined.Title
                        },
                        contentDescription = "Localized description"
                    )
                }
//                IconButton(onClick = {
//                    onPinClick(editNotesUiState.copy(note = editNotesUiState.note.copy(isPinned = !editNotesUiState.note.isPinned)))
//                }) {
//                    Icon(
//                        if (currentNote.isPinned) {
//                            Icons.Rounded.PushPin
//                        } else {
//                            Icons.Outlined.PushPin
//                        },
//                        contentDescription = "Localized description"
//                    )
//                }
//                IconButton(onClick = {
//                    onArchiveClick(
//                        editNotesUiState.copy(
//                            note = editNotesUiState.note.copy(
//                                isArchived = !editNotesUiState.note.isArchived
//                            )
//                        )
//                    )
//                }) {
//                    Icon(
//                        if (currentNote.isArchived) {
//                            Icons.Rounded.Archive
//                        } else {
//                            Icons.Outlined.Archive
//                        },
//                        contentDescription = "Localized description",
//                    )
//                }

                IconButton(onClick = {
                    val currentUri = NotesFileProvider.getImageUri(context)
                    uri = currentUri
                    cameraLauncher.launch(uri)
                }) {
                    Icon(Icons.Outlined.PhotoCamera, null)
                }

                if (!isNewNote) {
                    IconButton(onClick = {
                        onDeleteNote(currentNote.id)
                        onBackClick()
                    }) {
                        Icon(Icons.Outlined.Delete, null)
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onSaveClick(editNotesUiState) },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Rounded.Save, "Localized description")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CheckboxItem(
    modifier: Modifier = Modifier,
    checkListItem: Map.Entry<String, Boolean>,
    onCheckboxItemToggle: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onDeleteChecklistItem: (String) -> Unit,
    shouldFocus: Boolean = false,
    focusRequester: FocusRequester
) {
    val text = remember(checkListItem.key) {
        mutableStateOf(checkListItem.key)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Checkbox(checked = checkListItem.value, onCheckedChange = {
            onCheckboxItemToggle(checkListItem.key)
        })
        OutlinedTextField(
            maxLines = 1,
            modifier = modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        onValueChange(text.value)
                    }
                },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (checkListItem.value) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            ),
            value = text.value,
            onValueChange = {
                text.value = it
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = MaterialTheme.colorScheme.background
            ),
            keyboardActions = KeyboardActions(onDone = {
                onValueChange(text.value)
                keyboardController?.hide()
                focusManager.clearFocus()
            }),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        IconButton(onClick = {
            onDeleteChecklistItem(checkListItem.key)
        }) {
            Icon(Icons.Outlined.Close, null)
        }
    }

    LaunchedEffect(shouldFocus) {
        if (shouldFocus) {
            focusRequester.requestFocus()
        }
    }
}
