package com.codex.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codex.designsystem.R

@Composable
fun NotesSearchBar(
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    textField: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        tonalElevation = BottomAppBarDefaults.ContainerElevation,
    ) {
        Box(contentAlignment = Alignment.Center) {
            textField()
            navigationIcon()
            actions()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NotesSearchBarPreview() {
    NotesSearchBar(
        actions = {},
        navigationIcon = {

        },
        modifier = Modifier,
        textField = {
            TextField(
                value = "",
                onValueChange = {
                },
                leadingIcon = {
                    IconButton(
                        onClick = { /*TODO*/ }) {
                        Icon(Icons.Rounded.Menu, null)
                    }
                },
                placeholder = {
                    Text(text = stringResource(R.string.search_hint))
                }
            )
        }
    )
}