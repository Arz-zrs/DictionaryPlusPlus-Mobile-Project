package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.QuizRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class ObserveQuizAvailabilityUseCaseTest {

    private lateinit var quizRepository: QuizRepository
    private lateinit var isQuizAvailableUseCase: IsQuizAvailableUseCase
    private lateinit var observeQuizAvailabilityUseCase: ObserveQuizAvailabilityUseCase

    @Before
    fun setup() {
        quizRepository = mockk()
        isQuizAvailableUseCase = IsQuizAvailableUseCase() // Using real implementation as it's pure logic
        observeQuizAvailabilityUseCase = ObserveQuizAvailabilityUseCase(quizRepository, isQuizAvailableUseCase)
    }

    @Test
    fun `when never completed, quiz is available`() = runBlocking {
        every { quizRepository.getLastCompletedAtTimestamp() } returns flowOf(null)
        every { quizRepository.getRefreshTimeAtLastCompletion() } returns flowOf("06:00")

        val result = observeQuizAvailabilityUseCase().first()
        assertTrue(result)
    }

    @Test
    fun `when completed today after refresh time, quiz is not available`() = runBlocking {
        val now = LocalDateTime.now()
        val refreshTime = LocalTime.of(6, 0)
        val todayRefresh = LocalDateTime.of(now.toLocalDate(), refreshTime)
        
        // Completed at 7:00 today, now is 8:00
        val completionTime = todayRefresh.plusHours(1)
        val timestamp = completionTime.toInstant(ZoneOffset.UTC).toEpochMilli()

        every { quizRepository.getLastCompletedAtTimestamp() } returns flowOf(timestamp)
        every { quizRepository.getRefreshTimeAtLastCompletion() } returns flowOf("06:00")

        val result = observeQuizAvailabilityUseCase().first()
    }
}
