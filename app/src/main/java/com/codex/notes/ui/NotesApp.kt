package com.codex.notes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.codex.designsystem.component.NotesFloatingActionButton
import com.codex.notes.NotesActionComplete
import com.codex.notes.R
import com.codex.notes.navigation.NotesNavHost
import com.codex.notes.navigation.navigateToEditNotes
import com.codex.notes.navigation.notesRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(
    modifier: Modifier = Modifier,
    notesAppState: NotesAppState = rememberNotesAppState(
        navController = rememberNavController()
    )
) {
    val currentDestination: NavDestination? =
        notesAppState.navController.currentBackStackEntryAsState().value?.destination

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()

    var scrolledPastFirstItem by remember {
        mutableStateOf(false)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        floatingActionButton = {
            if (currentDestination?.route == null || currentDestination.route == notesRoute) {
                NotesFloatingActionButton(
                    modifier = modifier.padding(end = 8.dp, bottom = 32.dp),
                    onClick = {
                        notesAppState.navController.navigateToEditNotes()
                    },
                    titleRes = if (scrolledPastFirstItem) {
                        null
                    } else {
                        R.string.new_note
                    },
                    icon = Icons.Rounded.Add,
                    contentDescription = R.string.add_content_desc
                )
            }
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
//                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing
                        .only(WindowInsetsSides.Horizontal)
                )
        ) {
            NotesNavHost(navController = notesAppState.navController,
                onNotesActionComplete = { notesCount, action ->
                    coroutineScope.launch {
                        when (action) {
                            NotesActionComplete.Pin -> {
                                snackBarHostState.showSnackbar(message = "$notesCount notes pinned")
                            }
                            NotesActionComplete.Delete -> {
                                snackBarHostState.showSnackbar(message = "$notesCount notes deleted")
                            }
                            else -> {
                                snackBarHostState.showSnackbar(message = "$notesCount notes archived")
                            }
                        }
                    }
                },
                onScroll = {
                    scrolledPastFirstItem = it
                })
        }
    }
}

@Preview
@Composable
fun NotesAppPreview() {
    NotesApp()
}