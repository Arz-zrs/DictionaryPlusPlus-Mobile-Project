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
            var anchorWord = word ?: wordRepository.getRandomSeenWord() ?: "abandon"
            var definitionResult = definitionRepository.getDefinition(anchorWord)
            
            var definition = when (definitionResult) {
                is DefinitionResult.Success -> definitionResult.definition
                else -> {
                    if (word != null) return Result.failure(Exception("Could not fetch definition for $word"))

                    val fallbackResult = definitionRepository.getDefinition("abandon")
                    if (fallbackResult is DefinitionResult.Success) fallbackResult.definition
                    else return Result.failure(Exception("Could not fetch definition"))
                }
            }

            var attempts = 0
            while (definition.synonyms.isEmpty() && attempts < 5 && word == null) {
                attempts++
                anchorWord = wordRepository.getRandomSeenWord() ?: wordRepository.getRandomWords(1).firstOrNull() ?: "abandon"
                definitionResult = definitionRepository.getDefinition(anchorWord)
                if (definitionResult is DefinitionResult.Success) {
                    definition = definitionResult.definition
                }
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
                Result.failure(Exception("No synonyms found for $anchorWord"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
