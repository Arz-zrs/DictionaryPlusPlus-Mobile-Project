package com.example.dictionaryplusplus.core.util

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DenyListProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val denyList: Set<String> by lazy {
        try {
            val jsonString = context.assets.open("deny_list.json")
                .bufferedReader()
                .use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }.toSet()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            emptySet()
        }
    }
}
