package com.example.dictionaryplusplus.core.di

import android.content.Context
import androidx.room.Room
import com.example.dictionaryplusplus.data.local.AppDatabase
import com.example.dictionaryplusplus.data.local.dao.*
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "dictionary_plus_plus_db"
            )
            .fallbackToDestructiveMigration(true) // TODO: Remove fallback when migration is done
            .build()
    }

    @Provides
    fun provideWordDao(database: AppDatabase) : WordDao = database.wordDao()

    @Provides
    fun provideSeenEventDao(database: AppDatabase) : SeenEventDao = database.seenEventDao()

    @Provides
    fun provideDefinitionDao(database: AppDatabase) : DefinitionDao = database.definitionDao()

    @Provides
    fun provideWordNoteDao(database: AppDatabase) : WordNoteDao = database.wordNoteDao()

    @Provides
    fun provideFavouriteDao(database: AppDatabase) : FavouriteDao = database.favouriteDao()

    @Provides
    fun provideUserProfileDao(database: AppDatabase) : UserProfileDao = database.userProfileDao()
}