package com.example.dictionaryplusplus.ui.settings

import androidx.compose.runtime.Composable

@Composable
fun DebugSection(
    onTriggerWotd: () -> Unit,
    onTriggerDailyWord: () -> Unit,
    onResetQuiz: () -> Unit
) {
    // No-op for prod flavor
}
