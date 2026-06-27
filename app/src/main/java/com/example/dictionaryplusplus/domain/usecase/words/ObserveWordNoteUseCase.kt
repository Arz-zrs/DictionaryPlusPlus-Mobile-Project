package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWordNoteUseCase @Inject constructor(
    private val wordNoteRepository: WordNoteRepository
) {
    operator fun invoke(word: String): Flow<String> {
        return wordNoteRepository.observeWordNote(word)
    }
}
