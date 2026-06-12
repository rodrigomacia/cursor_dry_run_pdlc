package com.karaoke.app.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Configurações") })

        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                SettingsSectionHeader("Exibição", Icons.Default.Palette)
            }
            item {
                SettingsSliderItem(
                    title = "Tamanho da letra",
                    value = uiState.fontSize.toFloat(),
                    valueRange = 14f..36f,
                    displayValue = "${uiState.fontSize}sp",
                    onValueChange = { viewModel.setFontSize(it.toInt()) }
                )
            }
            item {
                SettingsSwitchItem(
                    title = "Tema escuro",
                    subtitle = "Usar fundo escuro (recomendado para karaokê)",
                    checked = uiState.darkTheme,
                    onCheckedChange = viewModel::setDarkTheme
                )
            }
            item {
                SettingsSwitchItem(
                    title = "Cores dinâmicas",
                    subtitle = "Adaptar cores ao wallpaper (Android 12+)",
                    checked = uiState.dynamicColor,
                    onCheckedChange = viewModel::setDynamicColor
                )
            }

            item {
                SettingsSectionHeader("Microfone", Icons.Default.Mic)
            }
            item {
                SettingsSliderItem(
                    title = "Ganho do microfone",
                    value = uiState.micGain,
                    valueRange = 0.5f..3f,
                    displayValue = "×${String.format("%.1f", uiState.micGain)}",
                    onValueChange = viewModel::setMicGain
                )
            }
            item {
                SettingsSliderItem(
                    title = "Sensibilidade de afinação",
                    value = uiState.pitchSensitivity,
                    valueRange = 0.1f..1f,
                    displayValue = "${(uiState.pitchSensitivity * 100).toInt()}%",
                    onValueChange = viewModel::setPitchSensitivity
                )
            }

            item {
                SettingsSectionHeader("Pontuação", Icons.Default.EmojiEvents)
            }
            item {
                SettingsSwitchItem(
                    title = "Ativar pontuação",
                    subtitle = "Calcular e exibir pontuação ao final da música",
                    checked = uiState.enableScoring,
                    onCheckedChange = viewModel::setEnableScoring
                )
            }
            item {
                SettingsSwitchItem(
                    title = "Guia de afinação",
                    subtitle = "Mostrar indicador visual de afinação durante a performance",
                    checked = uiState.showPitchGuide,
                    onCheckedChange = viewModel::setShowPitchGuide
                )
            }

            item {
                SettingsSectionHeader("Sobre", Icons.Default.Info)
            }
            item {
                AboutItem(version = uiState.appVersion)
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Medium)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingsSliderItem(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = title, fontWeight = FontWeight.Medium)
                Text(text = displayValue, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AboutItem(version: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Versão")
                Text(version, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "KaraokeApp usa músicas gratuitas do Jamendo (Creative Commons) e letras sincronizadas do LRCLib. Construído com Kotlin, Jetpack Compose e Media3.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
