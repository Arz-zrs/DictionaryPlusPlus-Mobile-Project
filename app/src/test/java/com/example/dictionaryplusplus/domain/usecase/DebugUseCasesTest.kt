package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.DebugRepository
import com.example.dictionaryplusplus.domain.usecase.quiz.ResetQuizCompletionUseCase
import com.example.dictionaryplusplus.domain.usecase.words.TriggerWotdWorkerUseCase
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DebugUseCasesTest {

    private lateinit var debugRepository: DebugRepository
    private lateinit var triggerWotdWorkerUseCase: TriggerWotdWorkerUseCase
    private lateinit var resetQuizCompletionUseCase: ResetQuizCompletionUseCase

    @Before
    fun setup() {
        debugRepository = mockk(relaxed = true)
        triggerWotdWorkerUseCase = TriggerWotdWorkerUseCase(debugRepository)
        resetQuizCompletionUseCase = ResetQuizCompletionUseCase(debugRepository)
    }

    @Test
    fun `triggerWotdWorkerUseCase should call repository`() {
        triggerWotdWorkerUseCase()
        verify { debugRepository.triggerWotdWorker() }
    }

    @Test
    fun `resetQuizCompletionUseCase should call repository`() = runBlocking {
        resetQuizCompletionUseCase()
        coVerify { debugRepository.resetQuizCompletion() }
    }
}
