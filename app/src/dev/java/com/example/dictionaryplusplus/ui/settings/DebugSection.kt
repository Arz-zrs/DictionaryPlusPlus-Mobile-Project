package com.example.dictionaryplusplus.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DebugSection(
    onTriggerWotd: () -> Unit,
    onTriggerDailyWord: () -> Unit,
    onResetQuiz: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Debug Tools (Dev Flavor)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onTriggerWotd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Trigger WotD Worker (API)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onTriggerDailyWord,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Trigger Daily Word Worker (Local)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onResetQuiz,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Quiz Completion Timestamp")
            }
        }
    }
}
