package com.example.dictionaryplusplus.ui.onboarding

enum class OnboardingStep(
    val showSkip: Boolean = false,
    val showNext: Boolean = true
) {
    WELCOME(showSkip = false, showNext = false),
    NOTIFICATIONS(showSkip = true, showNext = false),
    TIME_SELECTION(showSkip = true),
    FINISHED(showNext = false);

    fun next(): OnboardingStep? {
        val nextOrdinal = ordinal + 1
        return if (nextOrdinal < entries.size) entries[nextOrdinal] else null
    }
}