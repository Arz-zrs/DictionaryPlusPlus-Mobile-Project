package com.example.dictionaryplusplus.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dictionaryplusplus.data.local.dao.DefinitionCacheDao
import com.example.dictionaryplusplus.data.local.dao.FavoriteDao
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.UserProfileDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import com.example.dictionaryplusplus.data.local.entity.DefinitionCacheEntity
import com.example.dictionaryplusplus.data.local.entity.FavoriteEntity
import com.example.dictionaryplusplus.data.local.entity.UserProfileEntity
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity

@Database(
    entities = [
        WordEntity::class,
        SeenEventEntity::class,
        DefinitionCacheEntity::class,
        WordNoteEntity::class,
        FavoriteEntity::class,
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
    abstract fun favoriteDao(): FavoriteDao
    abstract fun userProfileDao(): UserProfileDao
}