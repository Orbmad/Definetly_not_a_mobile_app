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
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.googleid) //Icone aggiuntive per material
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation(libs.koin.androidx.compose.v340) //Dpendenza per richiamare Koin da composable
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}