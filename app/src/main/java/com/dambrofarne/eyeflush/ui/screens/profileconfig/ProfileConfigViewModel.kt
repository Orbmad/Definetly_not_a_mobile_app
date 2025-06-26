package com.dambrofarne.eyeflush.ui.screens.profileconfig

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImageStoringRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class ProfileConfigUiState(
    val username : String = "",
    val profileImageUrl: String = "",
    val usernameError: String? = null,
    val connectionError: String? = null,
    val isLoading: Boolean = false
)

sealed class UiEvent {
    data object OpenGallery : UiEvent()
}

class ProfileConfigViewModel(
    private val imageStoring : ImageStoringRepository,
    private val db : DatabaseRepository,
    private val auth : AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileConfigUiState())
    val uiState: StateFlow<ProfileConfigUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    suspend fun checkNotifications(): Boolean {
        val uId = auth.getCurrentUserId()
        if (uId != null) {
            return db.hasUnreadNotifications(uId)
        }
        return false
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPickPhotoClick(){
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.OpenGallery)
        }
    }

    suspend fun onImageSelected(uri: Uri) {
        val result = imageStoring.uploadImage(uri)
        _uiState.value = _uiState.value.copy(
            connectionError = null,
        )

        result.fold(
            onSuccess = { newImageAddress ->
                auth.getCurrentUserId()?.let { userId ->
                    db.changeProfileImage(userId, newImageAddress)
                    _uiState.value = _uiState.value.copy(profileImageUrl = newImageAddress)
                } ?: run {
                    _uiState.value = _uiState.value.copy(connectionError =  "We are sorry, we couldn't upload the image")
                }
            },
            onFailure = {
                _uiState.value = _uiState.value.copy(connectionError =  "We are sorry, we couldn't upload the image")
            }
        )
    }

    fun loadUserProfileImage() {
        viewModelScope.launch {
            val userId = auth.getCurrentUserId() ?: return@launch
            try {
                val imagePath = db.getUserImagePath(userId)
                _uiState.value = _uiState.value.copy(profileImageUrl = imagePath)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(profileImageUrl = "")
            }
            _uiState.value = _uiState.value.copy(username = db.getUsername(userId))
        }
    }

    fun setUsername(navToHome: () -> Unit) {
        val username = _uiState.value.username

        if (username.length < 5) {
            _uiState.value = _uiState.value.copy(
                usernameError = "Username must be at least 5 characters long"
            )
            return
        }else if (username.length > 12) {
            _uiState.value = _uiState.value.copy(
                usernameError = "Username shouldn't be longer than 12 characters"
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            usernameError = null,
            connectionError = null,
            isLoading = true
        )

        viewModelScope.launch {
            val userId = auth.getCurrentUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    connectionError = "User not authenticated"
                )
                return@launch
            }

            val taken = db.isUsernameTaken(username)
            if (taken) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    usernameError = "Username unavailable, somebody already has it"
                )
            } else {
                db.changeUsername(userId, username)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                )
                navToHome()
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }


}