package com.example.dictionaryplusplus.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dictionaryplusplus.data.local.dao.*
import com.example.dictionaryplusplus.data.local.entity.*

@Database(
    entities = [
        WordEntity::class,
        SeenEventEntity::class,
        DefinitionCacheEntity::class,
        WordNoteEntity::class,
        FavouriteEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun seenEventDao(): SeenEventDao
    abstract fun definitionCacheDao(): DefinitionCacheDao
    abstract fun wordNoteDao(): WordNoteDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun userProfileDao(): UserProfileDao
}