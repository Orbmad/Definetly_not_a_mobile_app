package com.dambrofarne.eyeflush


import com.dambrofarne.eyeflush.data.managers.camera.CameraManager
import com.dambrofarne.eyeflush.data.managers.camera.CameraManagerImpl
import com.dambrofarne.eyeflush.data.managers.location.LocationManager
import com.dambrofarne.eyeflush.data.managers.location.LocationManagerImpl
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.auth.FirebaseAuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.FirestoreDatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImageStoringRepository
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImgurImageStoringRepository
import com.dambrofarne.eyeflush.ui.screens.camera.CameraViewModel
import com.dambrofarne.eyeflush.ui.screens.gamification.GamificationViewModel
import com.dambrofarne.eyeflush.ui.screens.home.HomeMapViewModel
import com.dambrofarne.eyeflush.ui.screens.profile.ProfileViewModel
import com.dambrofarne.eyeflush.ui.screens.profileconfig.ProfileConfigViewModel
import com.dambrofarne.eyeflush.ui.screens.signin.SignInViewModel
import com.dambrofarne.eyeflush.ui.screens.signup.SignUpViewModel
import com.dambrofarne.eyeflush.ui.screens.splash.SplashViewModel
import com.dambrofarne.eyeflush.ui.screens.markerOverview.MarkerOverviewViewModel
import com.dambrofarne.eyeflush.ui.screens.notifications.NotificationViewModel
import com.dambrofarne.eyeflush.ui.screens.userOverview.UserOverviewViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single<AuthRepository> { FirebaseAuthRepository(get()) }
    single<DatabaseRepository> { FirestoreDatabaseRepository(get()) }
    single<ImageStoringRepository> { ImgurImageStoringRepository(get()) }
    single<CameraManager> { CameraManagerImpl(get()) }
    single<LocationManager> { LocationManagerImpl(get()) }

    viewModel { SignInViewModel(get(), get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { ProfileConfigViewModel(get(), get(), get()) }
    viewModel { HomeMapViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { CameraViewModel(get(), get(), get(), get(), get()) }
    viewModel { MarkerOverviewViewModel(get(), get()) }
    viewModel { NotificationViewModel(get(), get()) }
    viewModel { GamificationViewModel(get(), get()) }
    viewModel { UserOverviewViewModel(get(), get()) }

}
