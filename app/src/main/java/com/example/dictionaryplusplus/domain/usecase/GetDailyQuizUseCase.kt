package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.repository.WordRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetDailyQuizUseCase @Inject constructor(
    private val getDefinitionQuizUseCase: GetDefinitionQuizUseCase,
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(
        count: Int = 5,
        wordList: List<String> = emptyList()
    ): Result<List<QuizQuestion>> = coroutineScope {
        try {
            val questions = mutableListOf<QuizQuestion>()
            val excludedWords = mutableSetOf<String>()
            val maxAttempts = count * 4

            val seededWords = wordList.ifEmpty {
                wordRepository.getRandomWords(limit = count)
            }
            val queue = ArrayDeque(seededWords)
            var attempts = 0
            
            while (questions.size < count && attempts < maxAttempts) {
                val word = queue.removeFirstOrNull()
                    ?: wordRepository.getRandomWords(limit = 1).firstOrNull()
                    ?: break
                attempts++

                if (word in excludedWords) continue
                getDefinitionQuizUseCase(word).onSuccess { question ->
                    if (question.word !in excludedWords) {
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
