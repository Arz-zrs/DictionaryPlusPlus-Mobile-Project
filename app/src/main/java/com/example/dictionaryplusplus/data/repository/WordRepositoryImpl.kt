package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.domain.mapper.Word
import com.example.dictionaryplusplus.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {
    override fun searchWords(query: String): Flow<List<Word>> {
        return wordDao.searchWords(query).map { entities ->
            entities.map { Word(it.word, it.frequency) }
        }
    }
}