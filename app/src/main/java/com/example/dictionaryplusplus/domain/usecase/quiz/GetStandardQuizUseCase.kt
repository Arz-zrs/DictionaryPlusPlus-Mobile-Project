package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.repository.WordRepository
import javax.inject.Inject

class GetStandardQuizUseCase @Inject constructor(
    private val getDefinitionQuizUseCase: GetDefinitionQuizUseCase,
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(
        count: Int = 5,
        wordList: List<String> = emptyList()
    ): Result<List<QuizQuestion>> {
        return try {
            val questions = mutableListOf<QuizQuestion>()
            val excludedWords = mutableSetOf<String>()

            val initialWords = wordList.ifEmpty {
                wordRepository.getRandomWords(limit = count * 2)
            }
            val queue = ArrayDeque(initialWords)
            var attempts = 0
            val maxAttempts = count * 4

            while (questions.size < count && attempts < maxAttempts) {
                attempts++

                if (queue.isEmpty()) {
                    val fallback = wordRepository.getRandomWords(limit = count)
                        .filter { it !in excludedWords }
                    if (fallback.isEmpty()) break
                    queue.addAll(fallback)
                }

                val word = queue.removeFirstOrNull() ?: break
                if (word in excludedWords) continue

                getDefinitionQuizUseCase(word).onSuccess { question ->
                    if (question.word !in excludedWords && questions.size < count) {
                        questions.add(question)
                        excludedWords.add(question.word)
                    }
                }
            }

            if (questions.isNotEmpty()) Result.success(questions)
            else Result.failure(IllegalStateException("Could not generate quiz questions"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
