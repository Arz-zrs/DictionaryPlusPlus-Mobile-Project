package com.example.dictionaryplusplus.core.di

import com.example.dictionaryplusplus.data.repository.DebugRepositoryImpl
import com.example.dictionaryplusplus.domain.repository.DebugRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DebugModule {
    @Binds
    @Singleton
    abstract fun bindDebugRepository(
        debugRepositoryImpl: DebugRepositoryImpl
    ): DebugRepository
}
