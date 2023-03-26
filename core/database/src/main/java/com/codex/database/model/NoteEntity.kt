package com.codex.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codex.model.NoteContentType
import com.codex.model.data.Note
import kotlinx.datetime.Instant

@Entity(
    tableName = "notes"
)
data class NoteEntity(
    @PrimaryKey
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

fun NoteEntity.asExternalModel() = Note(
    id = id,
    title = title,
    content = content,
    type = type,
    checklist = checklist,
    createdAt = createdAt,
    lastEditedAt = lastEditedAt,
    isPinned = isPinned,
    isArchived = isArchived,
    labels = labels
)
