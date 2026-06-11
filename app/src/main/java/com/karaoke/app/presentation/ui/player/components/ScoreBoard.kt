package com.karaoke.app.presentation.ui.player.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karaoke.app.domain.model.ScoreGrade
import com.karaoke.app.domain.model.SongScore
import com.karaoke.app.presentation.theme.KaraokeGold
import com.karaoke.app.presentation.theme.KaraokeGreen
import com.karaoke.app.presentation.theme.KaraokePurple

@Composable
fun ScoreCardDialog(
    score: SongScore,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        ScoreCard(score = score, onDismiss = onDismiss)
    }
}

@Composable
fun ScoreCard(
    score: SongScore,
    onDismiss: () -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationStarted = true }

    val animatedScore by animateFloatAsState(
        targetValue = if (animationStarted) score.totalScore.toFloat() else 0f,
        animationSpec = tween(1500),
        label = "score_anim"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = KaraokeGold,
                modifier = Modifier.size(56.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = score.grade.label,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = when (score.grade) {
                    ScoreGrade.PERFECT -> KaraokeGold
                    ScoreGrade.GREAT -> KaraokeGreen
                    ScoreGrade.GOOD -> KaraokePurple
                    else -> MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Score ring
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animatedScore / 100f },
                    modifier = Modifier.size(120.dp),
                    strokeWidth = 10.dp,
                    color = KaraokePurple,
                    trackColor = KaraokePurple.copy(alpha = 0.15f)
                )
                Text(
                    text = "${animatedScore.toInt()}",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(24.dp))

            // Detail stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreStat(label = "Afinação", value = "${score.pitchScore}", unit = "pts")
                ScoreStat(label = "Ritmo", value = "${score.rhythmScore}", unit = "pts")
                ScoreStat(label = "Precisão", value = "${(score.accuracy * 100).toInt()}", unit = "%")
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(5) { i ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i < (score.totalScore / 20)) KaraokeGold else Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fechar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ScoreStat(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value$unit",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
