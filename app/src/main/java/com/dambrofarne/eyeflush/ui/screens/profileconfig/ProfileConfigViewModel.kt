package com.dambrofarne.eyeflush.ui.screens.profileconfig

import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.ui.screens.signin.SignInUiState
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

class ProfileConfigViewModel() : ViewModel() {
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

    fun onImageSelected(it: Uri) {
        //FAI COSE CON L'IMMAGINE
    }

}