package com.example.dictionaryplusplus.core.di

import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.data.repository.AuthRepositoryImpl
import com.example.dictionaryplusplus.data.repository.DefinitionRepositoryImpl
import com.example.dictionaryplusplus.data.repository.FavouriteRepositoryImpl
import com.example.dictionaryplusplus.data.repository.HistoryRepositoryImpl
import com.example.dictionaryplusplus.data.repository.LeaderboardRepositoryImpl
import com.example.dictionaryplusplus.data.repository.NotificationSchedulerImpl
import com.example.dictionaryplusplus.domain.repository.UserRepository
import com.example.dictionaryplusplus.data.repository.OnboardingRepositoryImpl
import com.example.dictionaryplusplus.data.repository.QuizRepositoryImpl
import com.example.dictionaryplusplus.data.repository.ScoreSyncSchedulerImpl
import com.example.dictionaryplusplus.data.repository.SettingsRepositoryImpl
import com.example.dictionaryplusplus.data.repository.UserRepositoryImpl
import com.example.dictionaryplusplus.data.repository.WordNoteRepositoryImpl
import com.example.dictionaryplusplus.domain.repository.WordRepository
import com.example.dictionaryplusplus.data.repository.WordRepositoryImpl
import com.example.dictionaryplusplus.data.repository.WotdRepositoryImpl
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import com.example.dictionaryplusplus.domain.repository.HistoryRepository
import com.example.dictionaryplusplus.domain.repository.LeaderboardRepository
import com.example.dictionaryplusplus.domain.repository.NotificationScheduler
import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import com.example.dictionaryplusplus.domain.repository.QuizRepository
import com.example.dictionaryplusplus.domain.repository.ScoreSyncScheduler
import com.example.dictionaryplusplus.domain.repository.SettingsRepository
import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindWordRepository(
        wordRepositoryImpl: WordRepositoryImpl
    ): WordRepository

    @Binds
    @Singleton
    abstract fun bindWordNoteRepository(
        wordNoteRepositoryImpl: WordNoteRepositoryImpl
    ): WordNoteRepository

    @Binds
    @Singleton
    abstract fun bindFavouriteRepository(
        favouriteRepositoryImpl: FavouriteRepositoryImpl
    ): FavouriteRepository

    @Binds
    @Singleton
    abstract fun bindDefinitionRepository(
        definitionRepositoryImpl: DefinitionRepositoryImpl
    ): DefinitionRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindWotdRepository(
        wotdRepositoryImpl: WotdRepositoryImpl
    ): WotdRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        onboardingRepositoryImpl: OnboardingRepositoryImpl
    ): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        quizRepositoryImpl: QuizRepositoryImpl
    ): QuizRepository

    @Binds
    @Singleton
    abstract fun bindNotificationScheduler(
        notificationSchedulerImpl: NotificationSchedulerImpl
    ): NotificationScheduler

    @Binds
    @Singleton
    abstract fun bindScoreSyncScheduler(
        scoreSyncSchedulerImpl: ScoreSyncSchedulerImpl
    ): ScoreSyncScheduler

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(
        leaderboardRepositoryImpl: LeaderboardRepositoryImpl
    ): LeaderboardRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}