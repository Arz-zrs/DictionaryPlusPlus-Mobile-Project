package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.WordRepository
import javax.inject.Inject

class GetPracticeQuizUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository,
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(word: String?): Result<QuizQuestion> {
        return try {
            val (anchorWord, definition) = if (word != null) {
                val result = definitionRepository.getDefinition(word)
                if (result is DefinitionResult.Success) {
                    word to result.definition
                } else {
                    return Result.failure(Exception("Could not fetch definition for requested word: $word"))
                }
            } else {
                var foundWord: String? = null
                var foundDef: Definition? = null

                repeat(5) {
                    val seenWord = wordRepository.getRandomSeenWord()
                    if (seenWord != null) {
                        val result = definitionRepository.getDefinition(seenWord)
                        if (result is DefinitionResult.Success) {
                            foundWord = seenWord
                            foundDef = result.definition
                            return@repeat
                        }
                    }
                }

                if (foundWord == null) {
                    val randomWords = wordRepository.getRandomWords(3)
                    for (randomWord in randomWords) {
                        val result = definitionRepository.getDefinition(randomWord)
                        if (result is DefinitionResult.Success) {
                            foundWord = randomWord
                            foundDef = result.definition
                            break
                        }
                    }
                }

                if (foundWord == null) {
                    val fallback = "abandon"
                    val result = definitionRepository.getDefinition(fallback)
                    if (result is DefinitionResult.Success) {
                        foundWord = fallback
                        foundDef = result.definition
                    }
                }

                if (foundWord == null || foundDef == null) {
                    return Result.failure(Exception("Could not find a valid word for practice quiz"))
                }
                foundWord to foundDef
            }

            val rawSynonyms = definition.synonyms
            val distractors = wordRepository.getRandomDistractors(anchorWord, 3)

            val (correctAnswer, isFallback) = if (rawSynonyms.isEmpty()) {
                Pair(anchorWord, true)
            } else {
                Pair(rawSynonyms.first(), false)
            }

            val choicesList = (distractors + correctAnswer).shuffled()

            Result.success(
                QuizQuestion(
                    word = anchorWord,
                    choices = choicesList,
                    correctAnswerIndex = choicesList.indexOf(correctAnswer),
                    originalDefinition = definition.definition,
                    isFallbackToDefinition = isFallback
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
