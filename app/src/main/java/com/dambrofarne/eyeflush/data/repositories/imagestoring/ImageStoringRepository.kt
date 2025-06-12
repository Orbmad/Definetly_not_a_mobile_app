package com.dambrofarne.eyeflush.data.repositories.imagestoring

import android.graphics.drawable.Drawable
import android.net.Uri

interface ImageStoringRepository {
    suspend fun uploadImage(uri: Uri): Result<String>
    suspend fun getDrawableImage(uri : Uri): Result<Drawable>
}