package com.example.dictionaryplusplus.domain.model

enum class MasteryStatus {
    LEARNING,
    MASTERED;

    override fun toString(): String {
        return name.lowercase()
    }

    companion object {
        fun fromString(value: String): MasteryStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: LEARNING
        }
    }
}