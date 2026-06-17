package com.example.dictionaryplusplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.dictionaryplusplus.navigation.NavigationGraph
import com.example.dictionaryplusplus.navigation.Screen
import com.example.dictionaryplusplus.ui.theme.DictionaryPlusPlusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DictionaryPlusPlusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationGraph(
                        navController = rememberNavController(),
                        startDestination = Screen.Onboarding.route,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}