package com.dambrofarne.eyeflush.data.repositories.imagestoring

import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.File

interface ImageStoringRepository {
    suspend fun uploadImage(uri: Uri): Result<String>
    suspend fun uploadImage(file: File): Result<String>
    suspend fun getDrawableImage(uri : Uri): Result<Drawable>
}