package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.data.remote.ApiResponse
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.WordRepository
import javax.inject.Inject

class GetSynonymQuizUC @Inject constructor(
    private val definitionRepository: DefinitionRepository,
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(word: String): Result<QuizQuestion> {
        return try {
            val definitionResult = definitionRepository.getDefinition(word)
            
            val definition = when (definitionResult) {
                is ApiResponse.Success -> definitionResult.data
                is ApiResponse.Error -> return Result.failure(Exception("API Error: ${definitionResult.errorType}"))
                ApiResponse.Loading -> return Result.failure(Exception("API is still loading"))
            }

            val rawSynonyms = definition.synonyms
            val distractors = wordRepository.getRandomDistractors(word, 3)

            if (rawSynonyms.isNotEmpty()) {
                val correctAnswer = rawSynonyms.first()
                val choicesList = (distractors + correctAnswer).shuffled()
                val correctIndex = choicesList.indexOf(correctAnswer)

                Result.success(
                    QuizQuestion(
                        word = word,
                        choices = choicesList,
                        correctAnswerIndex = correctIndex,
                        originalDefinition = definition.definition,
                        isFallbackToDefinition = false
                    )
                )
            } else {
                val choicesList = (distractors + word).shuffled()
                val correctIndex = choicesList.indexOf(word)

                Result.success(
                    QuizQuestion(
                        word = word,
                        choices = choicesList,
                        correctAnswerIndex = correctIndex,
                        originalDefinition = definition.definition,
                        isFallbackToDefinition = true
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}