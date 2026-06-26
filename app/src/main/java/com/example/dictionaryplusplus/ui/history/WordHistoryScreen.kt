package com.example.dictionaryplusplus.ui.history

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.ui.dictionary.WordDetailSheet
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.toastMessage.collectLatest { uiText ->
             Toast.makeText(context, uiText.asString(context), Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.btn_back)
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.history_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.history_empty_state),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                when (value) {
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        viewModel.removeHistoryEntry(item.id, item.word)
                                        true
                                    }
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        viewModel.toggleFavourite(item.word)
                                        false
                                    }
                                    else -> false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val targetValue = dismissState.targetValue
                                val color by animateColorAsState(
                                    targetValue = when (targetValue) {
                                        SwipeToDismissBoxValue.EndToStart ->
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                        SwipeToDismissBoxValue.StartToEnd ->
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        SwipeToDismissBoxValue.Settled -> Color.Transparent
                                    }
                                )
                                val alignment = when (targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                    SwipeToDismissBoxValue.Settled -> Alignment.Center
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 24.dp),
                                    contentAlignment = alignment
                                ) {
                                    when (targetValue) {
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete_content_description),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        SwipeToDismissBoxValue.StartToEnd -> {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = stringResource(R.string.favourite_content_description),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        SwipeToDismissBoxValue.Settled -> { }
                                    }
                                }
                            },
                            enableDismissFromStartToEnd = true
                        ) {
                            WordHistoryItem(
                                event = item,
                                onItemClick = { viewModel.onWordSelected(item.word) }
                            )
                        }
                    }
                }
            }
        }
        when (val state = uiState) {
            is HistorySheetState.WordDetail -> {
                WordDetailSheet(
                    word = state.word,
                    onDismiss = { viewModel.onSheetDismissed() }
                )
            }
            HistorySheetState.Hidden -> {}
        }
    }
}

@Composable
fun WordHistoryItem(
    event: HistoryUiState,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        onClick = onItemClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = event.word,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = event.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
