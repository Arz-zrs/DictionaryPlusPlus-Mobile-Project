package com.example.dictionaryplusplus.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun QuizScreen(onNavigateToSynonymQuiz: (String?) -> Unit, onNavigateToDailyQuiz: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Quiz Hub Screen Stub")
            Button(onClick = { onNavigateToSynonymQuiz(null) }) { Text("Start Synonym Practice") }
            Button(onClick = onNavigateToDailyQuiz) { Text("Start Daily Quiz") }
        }
    }
}