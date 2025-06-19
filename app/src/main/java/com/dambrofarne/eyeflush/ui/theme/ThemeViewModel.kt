package com.dambrofarne.eyeflush.ui.theme

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

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private var themeInitialized = false

    fun initTheme(systemDefault: Boolean) {
        if (themeInitialized) return
        themeInitialized = true

        viewModelScope.launch {
            val userId = auth.getCurrentUserId()
            val themeFromDb = userId?.let { db.getThemePreference(it) }
            _isDarkTheme.value = themeFromDb ?: systemDefault
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        viewModelScope.launch {
            auth.getCurrentUserId()?.let {
                db.changeThemePreference(it, enabled)
            }
        }
    }
}
