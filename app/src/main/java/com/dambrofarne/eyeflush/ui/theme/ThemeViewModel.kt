package com.dambrofarne.eyeflush.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.database.DatastoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val datastoreRepository: DatastoreRepository
) : ViewModel() {

    private val _themePreference = MutableStateFlow(ThemePreference.SYSTEM)
    val themePreference: StateFlow<ThemePreference> = _themePreference.asStateFlow()

    init {
        viewModelScope.launch {
            datastoreRepository.themePreferenceFlow.collectLatest { pref ->
                _themePreference.value = pref
            }
        }
    }

    fun setThemePreference(pref: ThemePreference) {
        viewModelScope.launch {
            datastoreRepository.saveThemePreference(pref)
        }
    }
}
