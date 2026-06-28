package com.example.dictionaryplusplus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.dao.FavouriteDao
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.UserProfileDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import com.example.dictionaryplusplus.data.local.dao.WotdHistoryDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.entity.FavouriteEntity
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.data.local.entity.UserProfileEntity
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity
import com.example.dictionaryplusplus.data.local.entity.WotdHistoryEntity

@Database(
    entities = [
        WordEntity::class,
        SeenEventEntity::class,
        DefinitionEntity::class,
        WordNoteEntity::class,
        FavouriteEntity::class,
        UserProfileEntity::class,
        WotdHistoryEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun seenEventDao(): SeenEventDao
    abstract fun definitionDao(): DefinitionDao
    abstract fun wordNoteDao(): WordNoteDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun wotdHistoryDao(): WotdHistoryDao
}