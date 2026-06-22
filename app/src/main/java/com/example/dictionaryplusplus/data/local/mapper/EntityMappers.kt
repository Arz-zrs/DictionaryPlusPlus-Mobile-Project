package com.example.dictionaryplusplus.data.local.mapper

import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.domain.model.Definition
import com.google.gson.Gson

fun DefinitionEntity.toDomain(gson: Gson): Definition {
    return Definition(
        word = word,
        definition = definition,
        phonetic = phonetic,
        exampleSentence = exampleSentence,
        synonyms = gson.fromJson(relatedWordsJson, Array<String>::class.java).toList()
    )
}