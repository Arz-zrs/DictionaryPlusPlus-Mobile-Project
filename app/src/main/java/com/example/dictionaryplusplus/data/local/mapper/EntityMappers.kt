package com.example.dictionaryplusplus.data.local.mapper

import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.entity.UserProfileEntity
import com.example.dictionaryplusplus.data.local.entity.WotdHistoryEntity
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.model.UserProfile
import com.example.dictionaryplusplus.domain.model.WordMeaning
import com.example.dictionaryplusplus.domain.model.WotdHistoryEntry
import com.example.dictionaryplusplus.domain.model.WotdSource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun DefinitionEntity.toDomain(gson: Gson): Definition {
    val meaningsType = object : TypeToken<List<Triple<String, String, String?>>>() {}.type
    val rawMeanings: List<Triple<String, String, String?>> = try {
        gson.fromJson(meaningsJson, meaningsType) ?: emptyList()
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        emptyList()
    }

    return Definition(
        word = word,
        definition = definition,
        phonetic = phonetic,
        partOfSpeech = partOfSpeech,
        exampleSentence = exampleSentence,
        synonyms = try {
            gson.fromJson(relatedWordsJson, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        },
        meanings = rawMeanings.map { (pos, def, ex) ->
            WordMeaning(pos, def, ex)
        }
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        userId = userId,
        displayName = displayName,
        email = email,
        totalScore = totalScore,
    )
}

fun WotdHistoryEntity.toDomain(): WotdHistoryEntry {
    val parsedSource = try {
        WotdSource.valueOf(source)
    } catch (e: IllegalArgumentException) {
        FirebaseCrashlytics.getInstance().recordException(e)
        WotdSource.LOCAL_FALLBACK
    }
    return WotdHistoryEntry(date = date, word = word, source = parsedSource)
}