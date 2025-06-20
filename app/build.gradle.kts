plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dambrofarne.eyeflush"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dambrofarne.eyeflush"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "IMGUR_CLIENT_ID", "\"${property("IMGUR_CLIENT_ID")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true //Mi server me recuperare il Client-ID id (Imgur)
        compose = true
    }
}

dependencies {
    implementation(libs.material3) //Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.firebase.auth.ktx)
    implementation(platform(libs.firebase.bom)) //Firebase
    implementation(libs.firebase.auth) //Firebase authentication
    implementation(libs.firebase.firestore) //Firebase Firestore
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.googleid) //Icone aggiuntive per material
    implementation(platform(libs.firebase.bom))  // Import the BoM for the Firebase platform
    implementation(libs.koin.androidx.compose.v340) //Dipendenza per richiamare Koin da composable
    implementation(libs.coil.compose) //Coil per la visualizzazione di immagini
    implementation(libs.kotlinx.coroutines.android) //Courtuin for imgur
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)


    //things Material
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)

    implementation(libs.androidx.material3.v112)
    implementation(libs.androidx.activity.compose.v180)
    implementation(libs.material.icons.extended)

    //Material
    implementation(libs.material3)

    //viewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // OpenStreetMap
    implementation(libs.osmdroid.android)

    // Camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.guava)
    implementation(libs.androidx.exifinterface)

    // Permissions
    implementation(libs.accompanist.permissions)

    // Image loading
    implementation(libs.coil.compose.v250)

    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2.v131)
    implementation(libs.androidx.camera.lifecycle.v131)
    implementation(libs.androidx.camera.view.v131)

// Permissions
    implementation(libs.accompanist.permissions)

// Material 3
    implementation (libs.material3)
    implementation(libs.androidx.material)
}