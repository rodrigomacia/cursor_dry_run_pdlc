package com.karaoke.app.presentation.ui.player.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaoke.app.domain.model.LyricLine
import com.karaoke.app.presentation.theme.LyricActive
import com.karaoke.app.presentation.theme.LyricHighlight
import com.karaoke.app.presentation.theme.LyricInactive

@Composable
fun LyricsDisplay(
    lyrics: List<LyricLine>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    fontSize: Int = 22
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0 && currentIndex < lyrics.size) {
            val target = (currentIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(target)
        }
    }

    if (lyrics.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♪  Letra não disponível  ♪",
                color = LyricInactive,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(lyrics) { index, line ->
            LyricLineItem(
                line = line,
                state = when {
                    index == currentIndex -> LyricState.ACTIVE
                    index < currentIndex -> LyricState.PAST
                    index == currentIndex + 1 -> LyricState.UPCOMING
                    else -> LyricState.FUTURE
                },
                fontSize = fontSize
            )
        }
    }
}

enum class LyricState { PAST, ACTIVE, UPCOMING, FUTURE }

@Composable
private fun LyricLineItem(
    line: LyricLine,
    state: LyricState,
    fontSize: Int
) {
    val targetColor = when (state) {
        LyricState.ACTIVE -> LyricActive
        LyricState.UPCOMING -> LyricHighlight
        LyricState.PAST -> LyricInactive.copy(alpha = 0.5f)
        LyricState.FUTURE -> LyricInactive.copy(alpha = 0.3f)
    }

    val color by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "lyric_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (state == LyricState.ACTIVE) 1.08f else 1f,
        animationSpec = tween(300),
        label = "lyric_scale"
    )

    Text(
        text = line.text,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = if (state == LyricState.ACTIVE) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .scale(scale)
    )
}
