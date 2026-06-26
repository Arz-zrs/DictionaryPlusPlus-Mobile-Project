package com.example.dictionaryplusplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.domain.model.ThemeMode
import com.example.dictionaryplusplus.ui.main.MainScreen
import com.example.dictionaryplusplus.ui.theme.DictionaryPlusPlusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            DictionaryPlusPlusTheme(darkTheme = darkTheme) {
                val currentDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = currentDensity.density,
                        fontScale = currentDensity.fontScale * fontSize.scale
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

                        startDestination?.let { destination ->
                            MainScreen(startDestination = destination)
                        } ?: Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val wordToSave = intent?.getStringExtra("EXTRA_WORD_TO_SAVE")
        viewModel.handleNotificationWord(wordToSave)
    }
}
