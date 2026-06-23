package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.WordRepository
import javax.inject.Inject

class GetSynonymQuizUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository,
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(word: String?): Result<QuizQuestion> {
        return try {
            val anchorWord = word ?: wordRepository.getRandomSeenWord() ?: "abandon"
            val definitionResult = definitionRepository.getDefinition(anchorWord)
            
            val definition = when (definitionResult) {
                is DefinitionResult.Success -> definitionResult.definition
                is DefinitionResult.Error -> return Result.failure(Exception("API Error: ${definitionResult.type}"))
                DefinitionResult.Loading -> return Result.failure(Exception("API is still loading"))
            }

            val rawSynonyms = definition.synonyms
            val distractors = wordRepository.getRandomDistractors(anchorWord, 3)

            if (rawSynonyms.isNotEmpty()) {
                val correctAnswer = rawSynonyms.first()
                val choicesList = (distractors + correctAnswer).shuffled()
                val correctIndex = choicesList.indexOf(correctAnswer)

                Result.success(
                    QuizQuestion(
                        word = anchorWord,
                        choices = choicesList,
                        correctAnswerIndex = correctIndex,
                        originalDefinition = definition.definition,
                        isFallbackToDefinition = false
                    )
                )
            } else {
                val choicesList = (distractors + anchorWord).shuffled()
                val correctIndex = choicesList.indexOf(anchorWord)

                Result.success(
                    QuizQuestion(
                        word = anchorWord,
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
