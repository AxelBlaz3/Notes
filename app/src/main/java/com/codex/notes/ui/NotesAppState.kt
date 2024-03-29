package com.codex.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberNotesAppState(
    navController: NavHostController = rememberNavController()
): NotesAppState {
    return remember(navController) {
        NotesAppState(navController = navController)
    }
}

@Stable
class NotesAppState(
    val navController: NavHostController
) {

    val currentDestination: NavDestination?
         @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination


}