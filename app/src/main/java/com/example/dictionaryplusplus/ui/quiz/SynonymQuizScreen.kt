package com.example.dictionaryplusplus.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SynonymQuizScreen(word: String?, onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Synonym Quiz Practice Screen Stub (Anchor word: ${word ?: "Random"})")
            Button(onClick = onNavigateBack) { Text("Back to Hub") }
        }
    }
}
