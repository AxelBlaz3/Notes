package com.codex.model.data

import com.codex.model.NoteContentType
import kotlinx.datetime.Instant

/**
 * External data layer representation of Note
 */
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val type: NoteContentType,
    val checklist: Map<String, Boolean>,
    val createdAt: Instant,
    val lastEditedAt: Instant,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val labels: ArrayList<String>
)
