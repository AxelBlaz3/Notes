package com.codex.notes

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codex.designsystem.R
import com.codex.designsystem.component.NotesAppBar
import com.codex.designsystem.component.NotesSearchBar
import com.codex.model.data.Note
import com.codex.ui.NoteCard

@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    onNoteClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    onNotesActionComplete: (Int, NotesActionComplete) -> Unit,
    onScroll: (Boolean) -> Unit,
) {
    val notesUiState by viewModel.notesUiState.collectAsStateWithLifecycle()
    val notesViewState by viewModel.notesViewState.collectAsStateWithLifecycle()
    val notesSearchState by viewModel.notesSearchState.collectAsStateWithLifecycle()
    val notesOrderState by viewModel.notesOrderState.collectAsStateWithLifecycle()
    val selectedNoteIdsState by viewModel.selectedNoteIds.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        onNoteClick = onNoteClick,
        notesUiState = notesUiState,
        onViewToggle = viewModel::toggleNotesView,
        notesViewState = notesViewState,
        notesSearchState = notesSearchState,
        notesOrderState = notesOrderState,
        onSearch = viewModel::onSearch,
        onNotesOrderClick = viewModel::sortNotesBy,
        selectedNoteIds = selectedNoteIdsState,
        onNoteSelected = viewModel::onNoteSelected,
        onCancelNotesSelection = viewModel::onCancelNotesSelection,
        onPinNotes = viewModel::pinNotes,
        onArchiveNotes = viewModel::archiveNotes,
        onDeleteNotes = viewModel::deleteNotes,
        onNotesActionComplete = onNotesActionComplete,
        onScroll = onScroll
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNoteClick: (String) -> Unit,
    notesUiState: NotesUiState,
    notesSearchState: NotesSearchState,
    notesViewState: NotesViewState,
    notesOrderState: NotesOrder,
    onSearch: (String) -> Unit,
    onViewToggle: () -> Unit,
    onNotesOrderClick: (NotesOrder) -> Unit,
    selectedNoteIds: Set<String>,
    onNoteSelected: (String) -> Unit,
    onCancelNotesSelection: () -> Unit,
    onArchiveNotes: (Set<String>) -> Unit,
    onPinNotes: (Set<String>) -> Unit,
    onDeleteNotes: (Set<String>) -> Unit,
    onNotesActionComplete: (Int, NotesActionComplete) -> Unit,
    onScroll: (Boolean) -> Unit
) {
    val openDialog = remember {
        mutableStateOf(false)
    }

    if (openDialog.value) {
        SortAlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            onNotesOrderClick = onNotesOrderClick,
            notesOrderState = notesOrderState
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.animateContentSize()) {
            if (selectedNoteIds.isEmpty()) {
                NotesSearchBar(
                    modifier = modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(16.dp),
                    actions = {
                    }, navigationIcon = { /*TODO*/ }) {
                    NotesSearchTextField(
                        text = notesSearchState.text,
                        onValueChange = onSearch,
                        onViewToggle = onViewToggle,
                        onSortClick = {
                            openDialog.value = true
                        },
                        notesViewState = notesViewState
                    )
                }
            } else {
                NotesAppBar(
                    modifier = modifier.padding(bottom = 16.dp),
                    title = {}, navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onCancelNotesSelection) {
                                Icon(Icons.Outlined.Close, null)
                            }
                            if (selectedNoteIds.isNotEmpty()) {
                                Text(
                                    text = "${selectedNoteIds.size}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }, actions = {
                        IconButton(onClick = {
                            onPinNotes(selectedNoteIds)
                            onCancelNotesSelection()
                            onNotesActionComplete(selectedNoteIds.size, NotesActionComplete.Pin)
                        }) {
                            Icon(Icons.Outlined.PushPin, null)
                        }
                        IconButton(onClick = {
                            onArchiveNotes(selectedNoteIds)
                            onCancelNotesSelection()
                            onNotesActionComplete(selectedNoteIds.size, NotesActionComplete.Archive)
                        }) {
                            Icon(Icons.Outlined.Archive, null)
                        }
                        IconButton(onClick = {
                            onDeleteNotes(selectedNoteIds)
                            onCancelNotesSelection()
                            onNotesActionComplete(selectedNoteIds.size, NotesActionComplete.Delete)
                        }) {
                            Icon(Icons.Outlined.Delete, null)
                        }
                    })
            }
        }

        if (notesUiState is NotesUiState.Success && notesUiState.notes.isNotEmpty()) {
            Crossfade(targetState = notesViewState) { notesViewState ->

                if (notesViewState is NotesViewState.List) {
                    NotesList(
                        notes = notesUiState.notes,
                        onNoteClick = onNoteClick,
                        selectedNoteIds = selectedNoteIds,
                        onSelected = onNoteSelected,
                        onScroll = onScroll
                    )
                } else {
                    NotesStaggeredGrid(
                        notes = notesUiState.notes,
                        onNoteClick = onNoteClick,
                        selectedNoteIds = selectedNoteIds,
                        onSelected = onNoteSelected
                    )
                }
            }
        } else {
            EmptyNotes()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    onNoteClick: (String) -> Unit,
    selectedNoteIds: Set<String>,
    notes: List<Note>,
    onScroll: (Boolean) -> Unit,
    onSelected: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()

    val scrolledPastFirstItem by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(key1 = scrolledPastFirstItem) {
        onScroll(scrolledPastFirstItem)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyListState,
        modifier = modifier
            .fillMaxHeight(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, bottom = 96.dp + WindowInsets.safeDrawing.only(
                WindowInsetsSides.Bottom
            ).asPaddingValues().calculateBottomPadding()
        )
    ) {
        items(notes, key = { note ->
            note.id
        }) { note ->
            Box(modifier = modifier.animateItemPlacement()) {
                NoteCard(
                    note = note,
                    onNoteClick = onNoteClick,
                    selectedNoteIds = selectedNoteIds,
                    onSelected = onSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesStaggeredGrid(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    selectedNoteIds: Set<String>,
    onNoteClick: (String) -> Unit,
    onSelected: (String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, bottom = 96.dp + WindowInsets.safeDrawing.only(
                WindowInsetsSides.Bottom
            ).asPaddingValues().calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        columns = StaggeredGridCells.Adaptive(minSize = 144.dp),
        modifier = modifier.fillMaxHeight()
    ) {
        items(notes, key = { note ->
            note.id
        }) { note ->
            NoteCard(
                note = note,
                onNoteClick = onNoteClick,
                selectedNoteIds = selectedNoteIds,
                onSelected = onSelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSearchTextField(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
    onSortClick: () -> Unit,
    onViewToggle: () -> Unit,
    notesViewState: NotesViewState
) {
    OutlinedTextField(
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(Icons.Rounded.Search, null)
        },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    onViewToggle()
                }) {
                    Icon(
                        if (notesViewState is NotesViewState.List) {
                            Icons.Outlined.GridView
                        } else {
                            Icons.Outlined.ViewAgenda
                        }, null
                    )
                }

                IconButton(onClick = onSortClick) {
                    Icon(Icons.Outlined.Sort, null)
                }
            }
        },
        placeholder = {
            Text(text = stringResource(R.string.search_hint))
        }
    )
}

@Composable
fun EmptyNotes(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.ListAlt,
            contentDescription = null,
            modifier = modifier
                .padding(bottom = 8.dp)
                .size(64.dp)
        )
        Text(text = "Saved notes show up here")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortAlertDialog(
    modifier: Modifier = Modifier,
    notesOrderState: NotesOrder,
    onNotesOrderClick: (NotesOrder) -> Unit,
    onDismissRequest: () -> Unit
) {
    val orderTypes: Map<String, NotesOrder> =
        mapOf(
            "Date created" to NotesOrder.DateCreated,
            "Last edited" to NotesOrder.LastEdited,
            "Title" to NotesOrder.Name
        )

    val selectedOrderKey = remember {
        mutableStateOf(orderTypes.filterValues { notesOrder ->
            notesOrder == notesOrderState
        }.keys.first())
    }

    AlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),

            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Sort, null, modifier.padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This will sort your notes by date created, edited or by title.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                Divider()

                orderTypes.forEach { noteOrderMap ->
                    TextButton(
                        modifier = modifier,
                        onClick = {
                            selectedOrderKey.value = noteOrderMap.key
                        }
                    ) {
                        if (selectedOrderKey.value == noteOrderMap.key) {
                            Icon(
                                Icons.Outlined.CheckCircleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            text = noteOrderMap.key,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(
                                    start =
                                    if (selectedOrderKey.value == noteOrderMap.key) {
                                        ButtonDefaults.IconSpacing
                                    } else {
                                        ButtonDefaults.IconSpacing + 24.dp
                                    }
                                )
                        )
                    }
                }

                Divider(modifier = modifier.padding(bottom = 8.dp))

                TextButton(
                    onClick = {
                        onNotesOrderClick(
                            orderTypes.getOrDefault(
                                selectedOrderKey.value,
                                defaultValue = notesOrderState
                            )
                        )

                        onDismissRequest()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onNoteClick = {},
        notesUiState = NotesUiState.Success(notes = emptyList()),
        onViewToggle = {},
        notesViewState = NotesViewState.List,
        notesSearchState = NotesSearchState(text = ""),
        onSearch = {},
        notesOrderState = NotesOrder.LastEdited,
        onNotesOrderClick = {},
        selectedNoteIds = setOf(),
        onNoteSelected = {},
        onCancelNotesSelection = {},
        onArchiveNotes = {},
        onPinNotes = {},
        onDeleteNotes = {},
        onNotesActionComplete = { count, action -> },
        onScroll = {}
    )
}