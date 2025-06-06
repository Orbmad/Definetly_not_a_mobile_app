package com.dambrofarne.eyeflush.data.managers.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor

class CameraManager(private val context: Context) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    suspend fun initializeCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        executor: Executor
    ) {
        withContext(Dispatchers.IO) {
            cameraProvider = ProcessCameraProvider.getInstance(context).get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                // Handle exception
            }
        }
    }

    suspend fun capturePhoto(): Uri? {
        return withContext(Dispatchers.IO) {
            val imageCapture = imageCapture ?: return@withContext null

            val photoFile = File(
                context.getExternalFilesDir(null),
                "photo_${System.currentTimeMillis()}.jpg"
            )

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            try {
                imageCapture.takePicture(
                    outputFileOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exception: ImageCaptureException) {
                            // Handle error
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            // Success handled by return value
                        }
                    }
                )
                Uri.fromFile(photoFile)
            } catch (e: Exception) {
                null
            }
        }
    }
}