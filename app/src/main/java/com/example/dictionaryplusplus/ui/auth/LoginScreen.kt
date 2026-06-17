package com.example.dictionaryplusplus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Login Screen Stub")
            Button(onClick = onLoginSuccess) { Text("Login Success -> Dashboard") }
            TextButton(onClick = onNavigateToRegister) { Text("No account? Register") }
        }
    }
}
