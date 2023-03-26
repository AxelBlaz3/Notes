package com.codex.database.di

import android.content.Context
import androidx.room.Room
import com.codex.database.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNotesDatabase(
        @ApplicationContext context: Context
    ): NotesDatabase = Room.databaseBuilder(
        context,
        NotesDatabase::class.java,
        "notes-database"
    ).build()
}