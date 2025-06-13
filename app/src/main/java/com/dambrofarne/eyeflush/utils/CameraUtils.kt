package com.dambrofarne.eyeflush.utils

import android.graphics.Bitmap

fun cropToCenterAspectRatio(bitmap: Bitmap, aspectRatio: Float): Bitmap {
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height

    val targetWidth = originalWidth
    val targetHeight = (targetWidth / aspectRatio).toInt()

    val finalWidth: Int
    val finalHeight: Int
    if (targetHeight > originalHeight) {
        finalHeight = originalHeight
        finalWidth = (finalHeight * aspectRatio).toInt()
    } else {
        finalWidth = targetWidth
        finalHeight = targetHeight
    }

    val left = (originalWidth - finalWidth) / 2
    val top = (originalHeight - finalHeight) / 2

    return Bitmap.createBitmap(bitmap, left, top, finalWidth, finalHeight)
}
