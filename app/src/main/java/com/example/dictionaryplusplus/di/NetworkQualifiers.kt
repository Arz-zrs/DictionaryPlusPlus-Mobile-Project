package com.example.dictionaryplusplus.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DictionaryRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WordnikRetrofit