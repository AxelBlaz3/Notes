package com.codex.data.di

import com.codex.data.repository.NotesRepository
import com.codex.data.repository.OfflineFirstNotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsNotesRepository(
        notesRepository: OfflineFirstNotesRepository
    ): NotesRepository
}