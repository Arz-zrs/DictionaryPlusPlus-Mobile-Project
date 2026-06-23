package com.example.dictionaryplusplus.di

import com.example.dictionaryplusplus.data.remote.DictionaryApiService
import com.example.dictionaryplusplus.data.remote.WordnikApiService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val DICTIONARY_API_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"
    const val WORDNIK_API_BASE_URL = "https://api.wordnik.com/v4/"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @DictionaryRetrofit
    fun provideDictionaryRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DICTIONARY_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @WordnikRetrofit
    fun provideWordnikRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WORDNIK_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideDictionaryApiService(
        @DictionaryRetrofit retrofit: Retrofit
    ): DictionaryApiService {
        return retrofit.create(DictionaryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWordnikApiService(
        @WordnikRetrofit retrofit: Retrofit
    ): WordnikApiService {
        return retrofit.create(WordnikApiService::class.java)
    }
}