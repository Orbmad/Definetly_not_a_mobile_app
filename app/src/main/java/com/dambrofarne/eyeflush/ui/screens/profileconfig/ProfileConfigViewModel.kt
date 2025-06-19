package com.dambrofarne.eyeflush.ui.screens.profileconfig

import android.net.Uri
import android.util.Log
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
    object OpenGallery : UiEvent()
    // data class ShowSnackbar(val message: String) : UiEvent()
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
                Log.d("ImgurUpload", "Immagine caricata: $newImageAddress")
                auth.getCurrentUserId()?.let { userId ->
                    db.changeProfileImage(userId, newImageAddress)
                    _uiState.value = _uiState.value.copy(profileImageUrl = newImageAddress)
                } ?: run {
                    Log.e("ProfileUpdate", "Utente non autenticato, non posso aggiunger l'immagine ")
                    _uiState.value = _uiState.value.copy(connectionError =  "Non è stato possibile caricare l'immagine")
                }
            },
            onFailure = { error ->
                Log.e("ImgurUpload", "Errore nel caricamento immagine", error)
                _uiState.value = _uiState.value.copy(connectionError =  "Non è stato possibile caricare l'immagine")
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
                Log.e("ProfileConfigVM", "Errore caricando immagine", e)
                _uiState.value = _uiState.value.copy(profileImageUrl = "")
            }
        }
    }

    fun setUsername(navToHome: () -> Unit) {
        val username = _uiState.value.username

        if (username.length < 5) {
            _uiState.value = _uiState.value.copy(
                usernameError = "Lo username deve avere almeno 5 caratteri"
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
                    connectionError = "Utente non autenticato"
                )
                return@launch
            }

            val taken = db.isUsernameTaken(username)
            if (taken) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    usernameError = "Username non disponibile"
                )
            } else {
                db.addUser(userId, username)

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