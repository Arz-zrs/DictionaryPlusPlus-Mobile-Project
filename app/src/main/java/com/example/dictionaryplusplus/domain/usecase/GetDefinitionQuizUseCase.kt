package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.core.util.ContentSanitizer
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
            val anchorWord = word ?: wordRepository.getRandomSeenWord() ?: wordRepository.getRandomWords(1).firstOrNull() ?: "abandon"
            val definitionResult = definitionRepository.getDefinition(anchorWord)
            
            val definition = when (definitionResult) {
                is DefinitionResult.Success -> definitionResult.definition
                else -> return Result.failure(Exception("Could not fetch definition for $anchorWord"))
            }

            val anchorDefinitionText = definition.definition
            if (ContentSanitizer.isFallbackDefinition(anchorDefinitionText)) {
                return Result.failure(Exception("Anchor definition was sanitized, skipping"))
            }

            val choices = mutableListOf(definition.definition)
            var attempts = 0
            while (choices.size < 4 && attempts < 10) {
                attempts++
                val distractorWords = wordRepository.getRandomDistractors(anchorWord, 5)
                for (dWord in distractorWords) {
                    val dResult = definitionRepository.getDefinition(dWord)
                    if (dResult is DefinitionResult.Success) {
                        val dDef = dResult.definition.definition
                        if (dDef.isNotBlank()
                            && !ContentSanitizer.isFallbackDefinition(dDef)
                            && !choices.contains(dDef)) {
                            choices.add(dDef)
                        }
                    }
                    if (choices.size == 4) break
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
