package com.codex.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codex.database.dao.NotesDao
import com.codex.database.model.NoteEntity
import com.codex.database.util.CheckListConverter
import com.codex.database.util.InstantConverter
import com.codex.database.util.LabelConverter
import com.codex.database.util.NoteContentTypeConvertor

@Database(
    entities = [
        NoteEntity::class
    ],
    version = 1
)
@TypeConverters(
    InstantConverter::class,
    NoteContentTypeConvertor::class,
    LabelConverter::class,
    CheckListConverter::class
)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao
}