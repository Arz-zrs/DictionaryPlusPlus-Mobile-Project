package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.DefinitionCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DefinitionCacheDao {
    @Query("SELECT * FROM definition_cache WHERE word = :word LIMIT 1")
    fun observeDefinition(word: String): Flow<DefinitionCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinition(definition: DefinitionCacheEntity)
}