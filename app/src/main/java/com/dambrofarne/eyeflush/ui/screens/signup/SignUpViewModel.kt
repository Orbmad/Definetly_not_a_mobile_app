package com.dambrofarne.eyeflush.ui.screens.signup

import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val connectionError: String? = null,
)

class SignUpViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onPasswordConfirmationChange(passwordConfirmation: String) {
        _uiState.value = _uiState.value.copy(passwordConfirmation = passwordConfirmation)
    }
    fun signUp(navToHome: () -> Unit){
        val email = _uiState.value.email
        val password = _uiState.value.password
        val passwordConfirmation = _uiState.value.passwordConfirmation
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "Email non può essere vuota")
            return
        }
        if (password.isBlank() or (password != passwordConfirmation)) {
            _uiState.value = _uiState.value.copy(passwordError = "Password non può essere vuota")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //_uiState.value = _uiState.value.copy(isLoading = false, isSignedIn = true)
                   // navToHome()
                } else {
                    val exception = task.exception
                    //_uiState.value = _uiState.value.copy(isLoading = false)
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
