package com.karaoke.app.presentation.ui.player.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaoke.app.presentation.theme.KaraokePink
import com.karaoke.app.presentation.theme.KaraokePurple
import com.karaoke.app.util.toMinutesSeconds

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    isMicEnabled: Boolean,
    currentPitch: Float,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress slider
        if (durationMs > 0) {
            Slider(
                value = currentPositionMs.toFloat() / durationMs,
                onValueChange = { fraction -> onSeek((fraction * durationMs).toLong()) },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = KaraokePurple,
                    activeTrackColor = KaraokePurple,
                    inactiveTrackColor = KaraokePurple.copy(alpha = 0.3f)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentPositionMs.toMinutesSeconds(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = durationMs.toMinutesSeconds(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic toggle button
            MicButton(
                isMicEnabled = isMicEnabled,
                pitch = currentPitch,
                onClick = onToggleMic
            )

            // Main play/pause button
            FilledIconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = KaraokePurple
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Tocar",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Restart button
            IconButton(onClick = { onSeek(0L) }) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Reiniciar",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun MicButton(
    isMicEnabled: Boolean,
    pitch: Float,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isMicEnabled) KaraokePink else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300),
        label = "mic_color"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(bgColor)
        ) {
            Icon(
                imageVector = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = if (isMicEnabled) "Desativar microfone" else "Ativar microfone",
                tint = if (isMicEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isMicEnabled && pitch > 0) {
            Text(
                text = "♪ ${pitch.toInt()} Hz",
                fontSize = 10.sp,
                color = KaraokePink,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
