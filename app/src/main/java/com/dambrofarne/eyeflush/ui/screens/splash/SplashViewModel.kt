package com.dambrofarne.eyeflush.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val auth: AuthRepository,
    private val db: DatabaseRepository
) : ViewModel() {

    private val _navigation = MutableSharedFlow<EyeFlushRoute>(replay = 0)
    val navigation = _navigation.asSharedFlow()

    init {
        navigateAfterDelay()
    }
    private fun navigateAfterDelay() {
        viewModelScope.launch {
            delay(2000)

            val userId = auth.getCurrentUserId()
            val route = when {
                userId != null && auth.isUserLoggedIn() && db.isUser(userId) -> {
                    EyeFlushRoute.Home
                }
                userId != null && auth.isUserLoggedIn() -> {
                    EyeFlushRoute.ProfileConfig()
                }
                else -> {
                    EyeFlushRoute.SignIn
                }
            }
            _navigation.emit(route)
        }
    }

}