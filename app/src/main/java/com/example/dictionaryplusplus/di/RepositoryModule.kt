package com.example.dictionaryplusplus.di

import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.data.repository.AuthRepositoryImpl
import com.example.dictionaryplusplus.data.repository.DefinitionCacheRepositoryImpl
import com.example.dictionaryplusplus.data.repository.FavouriteRepositoryImpl
import com.example.dictionaryplusplus.domain.repository.UserRepository
import com.example.dictionaryplusplus.data.repository.UserRepositoryImpl
import com.example.dictionaryplusplus.data.repository.WordNoteRepositoryImpl
import com.example.dictionaryplusplus.domain.repository.WordRepository
import com.example.dictionaryplusplus.data.repository.WordRepositoryImpl
import com.example.dictionaryplusplus.domain.repository.DefinitionCacheRepository
import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
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
    abstract fun bindDefinitionCacheRepository(
        definitionCacheRepositoryImpl: DefinitionCacheRepositoryImpl
    ): DefinitionCacheRepository
}