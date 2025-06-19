package com.dambrofarne.eyeflush.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val db: DatabaseRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private val _themePreference = MutableStateFlow(ThemePreference.SYSTEM)
    val themePreference: StateFlow<ThemePreference> = _themePreference.asStateFlow()

    private var themeInitialized = false

    fun initThemePreference() {
        if (themeInitialized) return
        themeInitialized = true

        viewModelScope.launch {
            val userId = auth.getCurrentUserId()
            if (userId != null) {
                try {
                    val stored = db.getThemePreferenceString(userId)
                    val pref = ThemePreference.fromString(stored)
                    _themePreference.value = pref
                } catch (e: Exception) {
                    _themePreference.value = ThemePreference.SYSTEM
                }
            } else {
                _themePreference.value = ThemePreference.SYSTEM
            }
        }
    }

    fun setThemePreference(pref: ThemePreference) {
        _themePreference.value = pref
        viewModelScope.launch {
            auth.getCurrentUserId()?.let { userId ->
                try {
                    db.changeThemePreferenceString(userId, pref.name)
                } catch (e: Exception) {
                    Log.e("ThemeViewModel", "setThemePreference: error while saving preference in DB", e)
                }
            }
        }
    }
}
