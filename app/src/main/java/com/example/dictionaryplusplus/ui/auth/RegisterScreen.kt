package com.example.dictionaryplusplus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Register Screen Stub")
            Button(onClick = onRegisterSuccess) { Text("Register Success -> Dashboard") }
            TextButton(onClick = onNavigateBack) { Text("Already have an account? Login") }
        }
    }
}
