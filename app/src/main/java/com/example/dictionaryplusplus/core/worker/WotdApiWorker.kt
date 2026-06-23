package com.example.dictionaryplusplus.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dictionaryplusplus.BuildConfig
import com.example.dictionaryplusplus.data.remote.WordnikApiService
import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.DefinitionErrorType
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WotdApiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val wotdRepository: WotdRepository,
    private val definitionRepository: DefinitionRepository,
    private val wordnikApiService: WordnikApiService
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val apiKey = BuildConfig.WORDNIK_API_KEY
            if (apiKey.isBlank()) return Result.failure()

            val response = wordnikApiService.fetchWordOfTheDay(apiKey)
            val word = response.word.trim().lowercase()
            
            if (word.isEmpty()) return Result.failure()

            wotdRepository.setWordOfTheDay(word)
            
            when (val definitionResult = definitionRepository.getDefinition(word)) {
                is DefinitionResult.Success -> Result.success()
                is DefinitionResult.Error -> {
                    if (definitionResult.type == DefinitionErrorType.NOT_FOUND) {
                        Result.failure()
                    } else {
                        Result.retry()
                    }
                }
                DefinitionResult.Loading -> Result.retry()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.retry()
        }
    }
}