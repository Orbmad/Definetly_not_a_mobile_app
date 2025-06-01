package com.dambrofarne.eyeflush.ui.screens.signin

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

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
            val result = auth.signInWithEmail(email, password)
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

    suspend fun requestGoogleCredential(context: Context): String? {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)  // false per permettere primo accesso
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken
            } else null
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(connectionError = "Errore login Google: ${e.localizedMessage}")
            null
        }
    }

    fun signInWithGoogle(
        idToken: String,
        navToHome: () -> Unit,
        navToProfileConfig: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = auth.signInWithGoogle(idToken)

            if (result.isSuccess) {
                val userId = auth.getCurrentUserId()
                val isRegistered = userId != null && db.isUser(userId)

                _uiState.value = _uiState.value.copy(isLoading = false, isSignedIn = true)

                if (isRegistered) {
                    navToHome()
                } else {
                    auth.getCurrentUserId()?.let { db.addUser(it) }
                    navToProfileConfig()
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    connectionError = "Login Google fallito."
                )
            }
        }
    }

}