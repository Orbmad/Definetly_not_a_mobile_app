package com.dambrofarne.eyeflush


import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.auth.FirebaseAuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.FirestoreDatabaseRepository
import com.dambrofarne.eyeflush.ui.screens.profileconfig.ProfileConfigViewModel
import com.dambrofarne.eyeflush.ui.screens.signin.SignInViewModel
import com.dambrofarne.eyeflush.ui.screens.signup.SignUpViewModel
import com.dambrofarne.eyeflush.ui.screens.splash.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { FirebaseAuth.getInstance()}
    single<AuthRepository> { FirebaseAuthRepository(get()) }
    single<DatabaseRepository> { FirestoreDatabaseRepository(get())}
    viewModel { SignInViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { ProfileConfigViewModel()}
}
