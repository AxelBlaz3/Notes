package com.codex.notes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.codex.notes.NotesActionComplete

@Composable
fun NotesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = notesRoute,
    onNotesActionComplete: (Int, NotesActionComplete) -> Unit,
    onScroll: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        notesScreen(
            onNoteClick = { noteId ->
                navController.navigateToEditNotes(
                    noteId = noteId
                )
            },
            onNotesActionComplete = onNotesActionComplete,
            onScroll = onScroll
        )
        editNotesScreen(
            onBackClick = navController::popBackStack,
        )
    }
}