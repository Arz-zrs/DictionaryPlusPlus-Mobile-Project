package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.WordRepository
import javax.inject.Inject

class GetDefinitionQuizUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository,
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(word: String?): Result<QuizQuestion> {
        return try {
            val anchorWord = word
                ?: wordRepository.getRandomSeenWord()
                ?: wordRepository.getRandomWords(1).firstOrNull()
                ?: "abandon"

            val definitionResult = definitionRepository.getDefinition(anchorWord)
            val definition = when (definitionResult) {
                is DefinitionResult.Success -> definitionResult.definition
                else -> return Result.failure(Exception("Could not fetch definition for $anchorWord"))
            }

            val choices = mutableSetOf(definition.definition)
            val fallbackDistractors = wordRepository.getRandomDistractors(anchorWord, limit = 20)

            for (dWord in fallbackDistractors) {
                if (choices.size >= 4) break

                val dDefinition = definitionRepository.getDefinitionOnce(dWord) ?: continue
                val dDef = dDefinition.definition

                if (dDef.isNotBlank() && dDef !in choices) {
                    choices.add(dDef)
                }
            }

            if (choices.size < 2) return Result.failure(Exception("Not enough definitions for quiz"))

            val shuffledChoices = choices.shuffled()
            Result.success(
                QuizQuestion(
                    word = anchorWord,
                    choices = shuffledChoices,
                    correctAnswerIndex = shuffledChoices.indexOf(definition.definition),
                    originalDefinition = definition.definition,
                    isFallbackToDefinition = false
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}