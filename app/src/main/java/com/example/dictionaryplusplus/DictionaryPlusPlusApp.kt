package com.example.dictionaryplusplus

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import com.example.dictionaryplusplus.core.di.ApplicationScope
import com.example.dictionaryplusplus.data.local.seeder.DefinitionSeeder
import com.example.dictionaryplusplus.data.local.seeder.WordBankSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class DictionaryPlusPlusApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var wordBankSeeder: WordBankSeeder
    @Inject lateinit var definitionSeeder: DefinitionSeeder
    @Inject @ApplicationScope lateinit var applicationScope: CoroutineScope

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch(Dispatchers.IO) {
            wordBankSeeder.seedWordBank()
            definitionSeeder.seedDefinitions()
        }
    }
}
