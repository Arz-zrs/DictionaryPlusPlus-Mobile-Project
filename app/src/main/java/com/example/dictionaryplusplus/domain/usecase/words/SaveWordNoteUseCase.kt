package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import javax.inject.Inject

class SaveWordNoteUseCase @Inject constructor(
    private val wordNoteRepository: WordNoteRepository
) {
    suspend operator fun invoke(word: String, note: String) {
        wordNoteRepository.saveWordNote(word, note)
    }
}
