package com.dambrofarne.eyeflush.ui.screens.signup

import android.util.Log
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
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository

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

class SignUpViewModel(
    private val auth: AuthRepository,
    private val db: DatabaseRepository
) : ViewModel() {

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
            _uiState.value = _uiState.value.copy(emailError = "Email field cannot be empty")
            return
        }

        if (password.isBlank()) {
            //Log.d("SignUp", "Password Vuota")
            _uiState.value = _uiState.value.copy(passwordError = "Password field cannot be empty")
            return
        }

        if (password != passwordConfirmation) {
            //Log.d("SignUp", "Le password non coincidono")
            _uiState.value = _uiState.value.copy(passwordError = "Le password non corrispondono")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = auth.signUpWithEmail(email, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedUp = true)
                auth.getCurrentUserId()?.let { db.addUser(it) }
                navToConfig()
            } else {
                val exception = result.exceptionOrNull()
                if (exception != null) {
                    Log.d("Log",exception.message.toString())
                };
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
