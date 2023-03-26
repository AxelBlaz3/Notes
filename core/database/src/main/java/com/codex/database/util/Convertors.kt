package com.codex.database.util

import androidx.room.TypeConverter
import com.codex.model.NoteContentType
import com.codex.model.asNoteContentType
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NoteContentTypeConvertor {
    @TypeConverter
    fun noteContentTypeToString(value: NoteContentType): String =
        value.serializedName

    @TypeConverter
    fun stringToNoteContentType(serializedName: String): NoteContentType =
        serializedName.asNoteContentType()
}

class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}

class LabelConverter {
    @TypeConverter
    fun listToString(value: ArrayList<String>): String =
        Json.encodeToString(value)

    @TypeConverter
    fun stringToList(value: String): ArrayList<String> =
        Json.decodeFromString(value)
}

class CheckListConverter {
    @TypeConverter
    fun listToString(value: Map<String, Boolean>): String =
        Json.encodeToString(value)

    @TypeConverter
    fun stringToList(value: String): Map<String, Boolean> =
        Json.decodeFromString(value)
}