package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.Word
import com.example.dictionaryplusplus.domain.repository.WordRepository
import com.example.dictionaryplusplus.domain.usecase.words.SearchWordsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchWordsUseCaseTest {
    private lateinit var wordRepository: WordRepository
    private lateinit var searchWordsUseCase: SearchWordsUseCase

    @Before
    fun setup() {
        wordRepository = mockk()
        searchWordsUseCase = SearchWordsUseCase(wordRepository)
    }

    @Test
    fun invoke_should_return_words_from_repository() = runTest {
        val query = "apple"
        val expectedWords = listOf(Word("apple"), Word("applet"))
        every { wordRepository.searchWords(query) } answers { flowOf(expectedWords) }  // ← only change

        val result = searchWordsUseCase(query).first()

        Assert.assertEquals(expectedWords, result)
        verify { wordRepository.searchWords(query) }
    }
}