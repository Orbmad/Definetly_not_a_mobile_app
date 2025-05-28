package com.dambrofarne.eyeflush.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.AuthRepository
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val auth: AuthRepository
) : ViewModel() {

    private val _navigation = MutableSharedFlow<EyeFlushRoute>(replay = 0)
    val navigation = _navigation.asSharedFlow()

    init {
        navigateAfterDelay()
    }

    private fun navigateAfterDelay() {
        viewModelScope.launch {
            delay(2000) //Caricamento di 2 secondi per la SplashScreen
            if (auth.isUserLoggedIn()) {
                _navigation.emit(EyeFlushRoute.Home)
            } else {
                _navigation.emit(EyeFlushRoute.SignIn)
            }
        }
    }
}