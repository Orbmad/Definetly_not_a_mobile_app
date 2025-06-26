package com.dambrofarne.eyeflush.utils

import android.graphics.Bitmap

private const val TARGET_ASPECT_RATIO = 4f/5f

fun cropToCenterAspectRatio(bitmap: Bitmap, aspectRatio: Float = TARGET_ASPECT_RATIO): Bitmap {
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height
    val originalRatio = originalWidth.toFloat() / originalHeight.toFloat()

    val cropWidth: Int
    val cropHeight: Int

    if (originalRatio > aspectRatio) {
        // Image larger: cut sides
        cropHeight = originalHeight
        cropWidth = (cropHeight * aspectRatio).toInt()
    } else {
        // Image higher: cut top/bottom
        cropWidth = originalWidth
        cropHeight = (cropWidth / aspectRatio).toInt()
    }

    val xOffset = (originalWidth - cropWidth) / 2
    val yOffset = (originalHeight - cropHeight) / 2

    return Bitmap.createBitmap(bitmap, xOffset, yOffset, cropWidth, cropHeight)
}


//fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
//    val matrix = Matrix().apply { postRotate(degrees) }
//    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//}

//fun fixImageOrientationAndCrop(photoFile: File, aspectRatio: Float = 4f / 5f): File {
//    val exif = ExifInterface(photoFile.absolutePath)
//    val orientation = exif.getAttributeInt(
//        ExifInterface.TAG_ORIENTATION,
//        ExifInterface.ORIENTATION_NORMAL
//    )
//
//    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
//    val rotatedBitmap = when (orientation) {
//        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
//        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
//        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
//        else -> bitmap
//    }
//
//    val croppedBitmap = cropToCenterAspectRatio(rotatedBitmap, aspectRatio)
//
//    // Save corrected image
//    FileOutputStream(photoFile).use { out ->
//        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//    }
//
//    // Reset EXIF orientation
//    val newExif = ExifInterface(photoFile.absolutePath)
//    newExif.setAttribute(
//        ExifInterface.TAG_ORIENTATION,
//        ExifInterface.ORIENTATION_NORMAL.toString()
//    )
//    newExif.saveAttributes()
//
//    return photoFile
//}

fun cropRelativeToOverlay(bitmap: Bitmap): Bitmap {
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height

    val overlayWidthRatio = 0.6f
    val overlayAspectRatio = 4f / 5f

    val cropWidth = (originalWidth * overlayWidthRatio).toInt()
    val cropHeight = (cropWidth / overlayAspectRatio).toInt()

    val xOffset = (originalWidth - cropWidth) / 2
    val yOffset = (originalHeight - cropHeight) / 2

    return Bitmap.createBitmap(bitmap, xOffset, yOffset, cropWidth, cropHeight)
}

