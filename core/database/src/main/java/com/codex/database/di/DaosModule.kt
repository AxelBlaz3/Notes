package com.codex.database.di

import com.codex.database.NotesDatabase
import com.codex.database.dao.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun provideNotesDao(
        notesDatabase: NotesDatabase
    ): NotesDao = notesDatabase.notesDao()
}