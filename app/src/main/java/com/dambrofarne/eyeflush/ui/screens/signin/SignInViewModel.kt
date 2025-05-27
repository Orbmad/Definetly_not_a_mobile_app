package com.dambrofarne.eyeflush.ui.screens.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import com.dambrofarne.eyeflush.data.repositories.AuthRepository
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val connectionError: String? = null,
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false
)


class SignInViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null, connectionError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null, connectionError = null)
    }

    fun signIn(navToHome: () -> Unit) {
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
            val result = repository.signInWithEmail(email, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedIn = true)
                navToHome()
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

    fun signInWithGoogle(context: Context, idToken: String, navToHome: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedIn = true)
                navToHome()
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, connectionError = "Login Google fallito.")
            }
        }
    }

    suspend fun requestGoogleCredential(context: Context): String? {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(com.dambrofarne.eyeflush.R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}