package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.repository.WordRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetDailyQuizUseCase @Inject constructor(
    private val getSynonymQuizUseCase: GetSynonymQuizUseCase,
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(
        count: Int = 5,
        wordList: List<String> = emptyList()
    ): Result<List<QuizQuestion>> = coroutineScope {
        try {
            val questions = mutableListOf<QuizQuestion>()
            val excludedWords = mutableSetOf<String>()
            
            val anchorWords = if (wordList.isNotEmpty()) {
                wordList.take(count)
            } else {
                wordRepository.getRandomWords(count)
            }

            for (word in anchorWords) {
                getSynonymQuizUseCase(word).onSuccess { question ->
                    if (!excludedWords.contains(question.word)) {
                        questions.add(question)
                        excludedWords.add(question.word)
                    }
                }
            }

            if (questions.isNotEmpty()) {
                Result.success(questions)
            } else {
                Result.failure(Exception("Could not generate quiz questions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
