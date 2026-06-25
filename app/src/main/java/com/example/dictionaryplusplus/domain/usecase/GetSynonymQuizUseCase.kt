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
                else -> return buildReverseDefinitionQuiz(word)
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
            if (rawSynonyms.isEmpty()) {
                val distractors = wordRepository.getRandomDistractors(anchorWord, 3)
                val choicesList = (distractors + anchorWord).shuffled()

                return Result.success(
                    QuizQuestion(
                        word = anchorWord,
                        choices = choicesList,
                        correctAnswerIndex = choicesList.indexOf(anchorWord),
                        originalDefinition = definition.definition,
                        isFallbackToDefinition = true
                    )
                )
            }
            val distractors = wordRepository.getRandomDistractors(anchorWord, 3)
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
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private suspend fun buildReverseDefinitionQuiz(word: String?): Result<QuizQuestion> {
        var anchorWord = word ?: wordRepository.getRandomSeenWord() ?: "abandon"
        var defResult = definitionRepository.getDefinition(anchorWord)

        var attempts = 0
        while (defResult !is DefinitionResult.Success && attempts < 5 && word == null) {
            attempts++
            anchorWord = wordRepository.getRandomSeenWord() ?: "abandon"
            defResult = definitionRepository.getDefinition(anchorWord)
        }

        val definition = (defResult as? DefinitionResult.Success)?.definition
            ?: return Result.failure(Exception("Could not generate fallback quiz"))

        val distractors = wordRepository.getRandomDistractors(anchorWord, 3)
        val choicesList = (distractors + anchorWord).shuffled()

        return Result.success(
            QuizQuestion(
                word = anchorWord,
                choices = choicesList,
                correctAnswerIndex = choicesList.indexOf(anchorWord),
                originalDefinition = definition.definition,
                isFallbackToDefinition = true
            )
        )
    }
}
