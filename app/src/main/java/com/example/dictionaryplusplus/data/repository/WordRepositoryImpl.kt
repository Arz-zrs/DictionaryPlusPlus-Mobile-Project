package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.domain.model.Word
import com.example.dictionaryplusplus.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val seenEventDao: SeenEventDao
) : WordRepository {
    override fun searchWords(query: String): Flow<List<Word>> {
        return wordDao.searchWords(query).map { entities ->
            entities.map { Word(it.word, it.frequency) }
        }
    }

    override suspend fun getRandomDistractors(
        excludedWord: String,
        limit: Int
    ): List<String> {
        return wordDao.getRandomDistractors(excludedWord, limit)
    }

    override suspend fun getRandomSeenWord(): String? {
        return seenEventDao.getRandomSeenWord()
    }
}
