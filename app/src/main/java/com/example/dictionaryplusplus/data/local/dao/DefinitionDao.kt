package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DefinitionDao {
    @Query("SELECT * FROM definition_cache WHERE word = :word LIMIT 1")
    fun observeDefinition(word: String): Flow<DefinitionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinition(definition: DefinitionEntity)

    @Query("SELECT * FROM definition_cache ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDefinition(): DefinitionEntity?
}