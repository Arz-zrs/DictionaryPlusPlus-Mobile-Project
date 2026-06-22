package com.example.dictionaryplusplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.domain.model.MasteryStatus
import com.example.dictionaryplusplus.ui.theme.Success

@Composable
fun MasteryChip(
    status: MasteryStatus,
    modifier: Modifier = Modifier,
) {
    val isMastered = status == MasteryStatus.MASTERED

    val backgroundColor =
        if (isMastered) Success.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)

    val textColor =
        if (isMastered) Success
        else MaterialTheme.colorScheme.primary

    val label = when (status) {
        MasteryStatus.LEARNING -> stringResource(R.string.status_learning)
        MasteryStatus.MASTERED -> stringResource(R.string.status_mastered)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
