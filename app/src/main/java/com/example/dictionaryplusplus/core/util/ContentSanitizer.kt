package com.example.dictionaryplusplus.core.util

import javax.inject.Inject

class ContentSanitizer @Inject constructor() {
    fun sanitizeText(text: String, denyList: Set<String>, fallback: String): String {
        val words = text.lowercase().split(Regex("[^a-zA-Z0-9']+"))
        val containsSensitiveWords = words.any { denyList.contains(it) }
        return if (containsSensitiveWords) fallback else text
    }

    fun sanitizeSynonyms(synonyms: List<String>, denyList: Set<String>): List<String> {
        return synonyms.filter { !denyList.contains(it.lowercase()) }
    }

    companion object {
        const val FALLBACK_DEFINITION = "Offensive definition omitted."
        const val FALLBACK_EXAMPLE = "Offensive example omitted."

        fun isFallbackDefinition(text: String): Boolean {
            return text == FALLBACK_DEFINITION
        }
    }
}