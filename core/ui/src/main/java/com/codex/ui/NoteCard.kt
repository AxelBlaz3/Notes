package com.codex.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codex.model.NoteContentType
import com.codex.model.data.Note
import kotlinx.datetime.Clock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (String) -> Unit,
    selectedNoteIds: Set<String>,
    onSelected: (String) -> Unit
) {
    val selected = selectedNoteIds.contains(note.id)

    OutlinedCard(
        border = BorderStroke(
            if (selected) {
                2.dp
            } else {
                1.dp
            },
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .combinedClickable(
                    onClick = {
                        if (selectedNoteIds.isEmpty()) {
                            onNoteClick(note.id)
                            return@combinedClickable
                        }

                        onSelected(note.id)
                    },
                    onLongClick = {
                        onSelected(note.id)
                    }
                )
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                note.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier.padding(bottom = 8.dp)
            )
            if (note.type == NoteContentType.SimpleText) {
                Text(note.content, style = MaterialTheme.typography.bodyMedium)
            } else if (note.type == NoteContentType.Checkboxes) {
                note.checklist.map { Pair(it.key, it.value) }.take(3).forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = it.second,
                            onCheckedChange = {},
                            enabled = false,
                            modifier = modifier.padding(
                                end = ButtonDefaults.IconSpacing
                            )
                        )
                        Text(
                            text = it.first, maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = if (it.second) {
                                    TextDecoration.LineThrough
                                } else {
                                    TextDecoration.None
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NoteCardPreview() {
    NoteCard(
        note = Note(
            id = "1",
            title = "This is a Note title",
            content = "This is some random note content. This can be pretty long.",
            type = NoteContentType.SimpleText,
            checklist = mutableMapOf(),
            createdAt = Clock.System.now(),
            lastEditedAt = Clock.System.now(),
            isPinned = false,
            isArchived = false,
            labels = arrayListOf()
        ),
        onNoteClick = {},
        onSelected = {},
        selectedNoteIds = setOf()
    )
}