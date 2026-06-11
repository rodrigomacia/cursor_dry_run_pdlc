package com.karaoke.app.presentation.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "karaoke_settings")

object SettingsKeys {
    val FONT_SIZE = intPreferencesKey("font_size")
    val MIC_GAIN = floatPreferencesKey("mic_gain")
    val PITCH_SENSITIVITY = floatPreferencesKey("pitch_sensitivity")
    val ENABLE_SCORING = booleanPreferencesKey("enable_scoring")
    val SHOW_PITCH_GUIDE = booleanPreferencesKey("show_pitch_guide")
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
}

data class SettingsUiState(
    val fontSize: Int = 22,
    val micGain: Float = 1f,
    val pitchSensitivity: Float = 0.5f,
    val enableScoring: Boolean = true,
    val showPitchGuide: Boolean = true,
    val darkTheme: Boolean = true,
    val dynamicColor: Boolean = true,
    val appVersion: String = "1.0.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _uiState.update { state ->
                    state.copy(
                        fontSize = prefs[SettingsKeys.FONT_SIZE] ?: 22,
                        micGain = prefs[SettingsKeys.MIC_GAIN] ?: 1f,
                        pitchSensitivity = prefs[SettingsKeys.PITCH_SENSITIVITY] ?: 0.5f,
                        enableScoring = prefs[SettingsKeys.ENABLE_SCORING] ?: true,
                        showPitchGuide = prefs[SettingsKeys.SHOW_PITCH_GUIDE] ?: true,
                        darkTheme = prefs[SettingsKeys.DARK_THEME] ?: true,
                        dynamicColor = prefs[SettingsKeys.DYNAMIC_COLOR] ?: true
                    )
                }
            }
        }
    }

    fun setFontSize(size: Int) = updatePref { it[SettingsKeys.FONT_SIZE] = size }
    fun setMicGain(gain: Float) = updatePref { it[SettingsKeys.MIC_GAIN] = gain }
    fun setPitchSensitivity(s: Float) = updatePref { it[SettingsKeys.PITCH_SENSITIVITY] = s }
    fun setEnableScoring(enabled: Boolean) = updatePref { it[SettingsKeys.ENABLE_SCORING] = enabled }
    fun setShowPitchGuide(show: Boolean) = updatePref { it[SettingsKeys.SHOW_PITCH_GUIDE] = show }
    fun setDarkTheme(dark: Boolean) = updatePref { it[SettingsKeys.DARK_THEME] = dark }
    fun setDynamicColor(dynamic: Boolean) = updatePref { it[SettingsKeys.DYNAMIC_COLOR] = dynamic }

    private fun updatePref(update: suspend (MutablePreferences) -> Unit) {
        viewModelScope.launch {
            context.dataStore.edit { prefs -> update(prefs) }
        }
    }
}
