package com.example.dictionaryplusplus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.dao.FavouriteDao
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.UserProfileDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.entity.FavouriteEntity
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.data.local.entity.UserProfileEntity
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity

@Database(
    entities = [
        WordEntity::class,
        SeenEventEntity::class,
        DefinitionEntity::class,
        WordNoteEntity::class,
        FavouriteEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun seenEventDao(): SeenEventDao
    abstract fun definitionDao(): DefinitionDao
    abstract fun wordNoteDao(): WordNoteDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun userProfileDao(): UserProfileDao
}