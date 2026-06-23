package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.Word
import com.example.dictionaryplusplus.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    operator fun invoke(query: String): Flow<List<Word>> {
        return wordRepository.searchWords(query)
    }
}
