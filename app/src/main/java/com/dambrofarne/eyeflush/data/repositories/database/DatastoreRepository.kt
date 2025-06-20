package com.dambrofarne.eyeflush.data.repositories.database

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dambrofarne.eyeflush.dataStore
import com.dambrofarne.eyeflush.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


class DatastoreRepository(private val context: Context) : LocalSettingsManager {

    private val themePref = stringPreferencesKey("theme_preference")

    override val themePreferenceFlow: Flow<ThemePreference> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[themePref]?.let { ThemePreference.fromString(it) } ?: ThemePreference.SYSTEM
        }

    override suspend fun saveThemePreference(themePreference: ThemePreference) {
        context.dataStore.edit { prefs ->
            prefs[themePref] = themePreference.name
        }
    }
}