package com.dambrofarne.eyeflush.data.managers.camera

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.concurrent.Executor

interface CameraManager {
    val cameraState: StateFlow<CameraState>
    val capturedImage: StateFlow<File?>

    fun initializeCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        executor: Executor
    )

    fun capturePhoto()

    fun confirmPhoto(): File?

    fun retryPhoto()

    // suspend fun savePhoto(file: File)

    fun resetCameraState()

    fun cleanup()
}