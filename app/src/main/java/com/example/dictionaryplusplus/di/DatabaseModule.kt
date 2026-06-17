package com.example.dictionaryplusplus.di

import android.app.Application
import androidx.room.Room
import com.example.dictionaryplusplus.data.AppDatabase
import com.example.dictionaryplusplus.data.local.dao.DefinitionCacheDao
import com.example.dictionaryplusplus.data.local.dao.FavoriteDao
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.UserProfileDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideWordDao(database: AppDatabase) : WordDao = database.wordDao()

    @Provides
    fun provideSeenEventDao(database: AppDatabase) : SeenEventDao = database.seenEventDao()

    @Provides
    fun provideDefinitionCacheDao(database: AppDatabase) : DefinitionCacheDao = database.definitionCacheDao()

    @Provides
    fun provideWordNoteDao(database: AppDatabase) : WordNoteDao = database.wordNoteDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase) : FavoriteDao = database.favoriteDao()

    @Provides
    fun provideUserProfileDao(database: AppDatabase) : UserProfileDao = database.userProfileDao()
}