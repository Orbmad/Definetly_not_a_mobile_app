package com.dambrofarne.eyeflush.data.repositories.imagestoring

import android.net.Uri

interface ImageStoringRepository {
    suspend fun uploadImage(uri: Uri): Result<String>
}