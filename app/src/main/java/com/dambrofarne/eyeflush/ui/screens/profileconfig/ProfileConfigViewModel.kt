package com.dambrofarne.eyeflush.ui.screens.profileconfig

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.ViewModel
import com.dambrofarne.eyeflush.ui.screens.signin.SignInUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProfileConfigUiState(
    val username : String = ""
)
class ProfileConfigViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileConfigUiState())
    val uiState: StateFlow<ProfileConfigUiState> = _uiState

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

}