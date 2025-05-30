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
    val username : String = ""
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

        result.fold(
            onSuccess = { newImageAddress ->
                Log.d("ImgurUpload", "Immagine caricata: $newImageAddress")
                auth.getCurrentUserId()?.let { userId ->
                    db.changeProfileImage(userId, newImageAddress)
                } ?: run {
                    Log.e("ProfileUpdate", "Utente non autenticato, non posso aggiunger l'immagine ")
                }
            },
            onFailure = { error ->
                Log.e("ImgurUpload", "Errore nel caricamento immagine", error)
                // TODO: mostra messaggio d’errore all’utente
            }
        )
    }

}