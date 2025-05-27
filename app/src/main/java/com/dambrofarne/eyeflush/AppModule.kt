package com.dambrofarne.eyeflush


import com.dambrofarne.eyeflush.data.repositories.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.FirebaseAuthRepository
import com.dambrofarne.eyeflush.ui.screens.signin.SignInViewModel
import com.dambrofarne.eyeflush.ui.screens.signup.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { FirebaseAuth.getInstance()}
    single<AuthRepository> { FirebaseAuthRepository(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
}
