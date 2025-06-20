package com.dambrofarne.eyeflush.data.repositories.database

import com.dambrofarne.eyeflush.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow

interface LocalSettingsManager {
    val themePreferenceFlow: Flow<ThemePreference>
    suspend fun saveThemePreference(themePreference: ThemePreference)
}