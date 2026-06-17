package com.example.dictionaryplusplus.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Onboarding Screen Stub")
            Button(onClick = onNavigateToLogin) { Text("Finish Onboarding -> Login") }
        }
    }
}
