package com.codex.database.dao

import androidx.room.*
import com.codex.database.model.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NoteEntity] access
 */
@Dao
interface NotesDao {

    @Query(
        "SELECT * FROM notes"
    )
    fun getNoteEntities(): Flow<List<NoteEntity>>

    /**
     * Inserts the [noteEntities] into the db if they don't exist and replaces those that do
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceNotes(noteEntities: List<NoteEntity>): List<Long>

    /**
     * Updates the [entities] in the db that match the primary key and no-ops if they don't
     */
    @Update
    suspend fun updateNotes(entities: List<NoteEntity>)

    /**
     * Deletes the notes in the db matching the specified [ids]
     */
    @Query(
        """DELETE FROM notes
           WHERE id in (:ids)
        """
    )
    suspend fun deleteNotes(ids: Set<String>)
}