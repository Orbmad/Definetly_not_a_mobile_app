package com.dambrofarne.eyeflush.ui.screens.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val connectionError: String? = null,
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false
)


class SignInViewModel(
    private val auth: AuthRepository,
    private val db: DatabaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null, connectionError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null, connectionError = null)
    }

    fun signIn(
        navToHome: () -> Unit,
        navToProfileConfig : () -> Unit
    ) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "Email non può essere vuota")
            return
        }
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Password non può essere vuota")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = auth.signInWithEmail(email, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedIn = true)
                auth.getCurrentUserId()?.let {
                    if(db.isUser(it)){
                        navToHome()
                    }else{
                        navToProfileConfig()
                    }
                }
            } else {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(isLoading = false)
                when (exception) {
                    is FirebaseAuthInvalidUserException -> _uiState.value = _uiState.value.copy(emailError = "Email non esiste.")
                    is FirebaseAuthInvalidCredentialsException -> _uiState.value = _uiState.value.copy(passwordError = "Password errata.")
                    is FirebaseNetworkException -> _uiState.value = _uiState.value.copy(connectionError = "Problemi di rete, ritenta.")
                    else -> _uiState.value = _uiState.value.copy(connectionError = "Errore sconosciuto, riprova.")
                }
            }
        }
    }
}