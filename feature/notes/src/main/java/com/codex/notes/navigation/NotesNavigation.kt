package com.codex.notes.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.codex.common.decoder.StringDecoder
import com.codex.notes.EditNotesRoute
import com.codex.notes.HomeRoute
import com.codex.notes.NotesActionComplete

const val editNoteArg: String = "noteId"

const val notesRoute = "notes"
const val editNotesRoute = "edit?noteId={noteId}"

internal class EditNoteArgs(val noteId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
            this(stringDecoder.decodeString(savedStateHandle[editNoteArg] ?: ""))
}

fun NavController.navigateToNotes(navOptions: NavOptions? = null) {
    this.navigate(route = notesRoute, navOptions = navOptions)
}

fun NavController.navigateToEditNotes(noteId: String = "", navOptions: NavOptions? = null) {
    val encodedId: String = Uri.encode(noteId)
    this.navigate(route = editNotesRoute.replace("{noteId}", encodedId), navOptions = navOptions)
}

fun NavGraphBuilder.notesScreen(
    onNoteClick: (String) -> Unit,
    onNotesActionComplete: (Int, NotesActionComplete) -> Unit,
    onScroll: (Boolean) -> Unit
) {
    composable(route = notesRoute) {
        HomeRoute(
            onNoteClick = onNoteClick,
            onNotesActionComplete = onNotesActionComplete,
            onScroll = onScroll
        )
    }
}

fun NavGraphBuilder.editNotesScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = editNotesRoute,
        arguments = listOf(navArgument(name = editNoteArg) {
            defaultValue = ""
        })
    ) { backStackEntry ->
        EditNotesRoute(
            onBackClick = onBackClick,
        )
    }
}