package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.DebugRepository
import com.example.dictionaryplusplus.domain.usecase.words.TriggerWotdWorkerUseCase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class DebugUseCasesTest {

    private lateinit var debugRepository: DebugRepository
    private lateinit var triggerWotdWorkerUseCase: TriggerWotdWorkerUseCase

    @Before
    fun setup() {
        debugRepository = mockk(relaxed = true)
        triggerWotdWorkerUseCase = TriggerWotdWorkerUseCase(debugRepository)
    }

    @Test
    fun `triggerWotdWorkerUseCase should call repository`() {
        triggerWotdWorkerUseCase()
        verify { debugRepository.triggerWotdWorker() }
    }
}
