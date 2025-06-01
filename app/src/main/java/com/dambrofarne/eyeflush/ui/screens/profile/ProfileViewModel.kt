package com.dambrofarne.eyeflush.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.ui.screens.profileconfig.ProfileConfigUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profileImageUrl: String = "",
    val username: String = "",
)


class ProfileViewModel(
    private val db : DatabaseRepository,
    private val auth : AuthRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadUserProfileInfo() {
        viewModelScope.launch {
            val userId = auth.getCurrentUserId() ?: return@launch
            try {
                val imagePath = db.getUserImagePath(userId)
                val username= db.getUsername(userId)
                _uiState.value = _uiState.value.copy(profileImageUrl = imagePath)
                _uiState.value = _uiState.value.copy(username = username)
            } catch (e: Exception) {
                Log.e("ProfileConfigVM", "Errore le info del profilo", e)
                _uiState.value = _uiState.value.copy(profileImageUrl = "")
                _uiState.value = _uiState.value.copy(username = "")

            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}