package com.dambrofarne.eyeflush.ui.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val connectionError: String? = null,
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false
)

class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null, connectionError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null, connectionError = null)
    }

    fun onPasswordConfirmationChange(passwordConfirmation: String) {
        _uiState.value = _uiState.value.copy(passwordConfirmation = passwordConfirmation, passwordError = null)
    }

    fun signUp(navToConfig: () -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val passwordConfirmation = _uiState.value.passwordConfirmation

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "Email non può essere vuota")
            return
        }

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Password non può essere vuota")
            return
        }

        if (password != passwordConfirmation) {
            _uiState.value = _uiState.value.copy(passwordError = "Le password non corrispondono")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repository.signUpWithEmail(email, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedUp = true)
                navToConfig()
            } else {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(isLoading = false)
                when (exception) {
                    is FirebaseAuthUserCollisionException -> {
                        _uiState.value = _uiState.value.copy(emailError = "Email già registrata.")
                    }
                    is FirebaseAuthWeakPasswordException -> {
                        _uiState.value = _uiState.value.copy(passwordError = "Password troppo debole.")
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        _uiState.value = _uiState.value.copy(emailError = "Formato email non valido.")
                    }
                    is FirebaseNetworkException -> {
                        _uiState.value = _uiState.value.copy(connectionError = "Problemi di rete, riprova.")
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(connectionError = "Errore sconosciuto, riprova.")
                    }
                }
            }
        }
    }
}
