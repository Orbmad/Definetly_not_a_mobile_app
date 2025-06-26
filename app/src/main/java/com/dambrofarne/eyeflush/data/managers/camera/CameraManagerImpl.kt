package com.dambrofarne.eyeflush.data.managers.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LifecycleOwner
import com.dambrofarne.eyeflush.utils.cropRelativeToOverlay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

class CameraManagerImpl(private val context: Context) : CameraManager {

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val _cameraState = MutableStateFlow(CameraState.IDLE)
    override val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private val _capturedImage = MutableStateFlow<File?>(null)
    override val capturedImage: StateFlow<File?> = _capturedImage.asStateFlow()

    private lateinit var previewView: PreviewView

    companion object {
        private const val TAG = "CameraManager"
        private const val JPEG_QUALITY = 95
    }

    @SuppressLint("MissingPermission")
    override fun initializeCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        executor: Executor
    ) {
        this.previewView = previewView
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                startCamera(previewView, lifecycleOwner)
                _cameraState.value = CameraState.READY
                //Log.d(TAG, "Camera initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize camera", e)
                _cameraState.value = CameraState.ERROR
            }
        }, executor)
    }

    @Suppress("DEPRECATION")
    private fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        try {
            val targetRotation = previewView.display.rotation

            val preview = Preview.Builder()
                .setTargetRotation(targetRotation)
                .build()
                .also { it.surfaceProvider = previewView.surfaceProvider }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(targetRotation)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                //.setTargetResolution(Size(1080, 1350))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setJpegQuality(JPEG_QUALITY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider?.apply {
                unbindAll()
                bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start camera", e)
            _cameraState.value = CameraState.ERROR
        }
    }

    override fun capturePhoto() {
        val imageCapture = this.imageCapture ?: run {
            Log.e(TAG, "ImageCapture is null")
            _cameraState.value = CameraState.ERROR
            return
        }

        if (_cameraState.value != CameraState.READY) {
            Log.w(TAG, "Camera not ready for capture. Current state: ${_cameraState.value}")
            return
        }

        _cameraState.value = CameraState.CAPTURING
        val photoFile = createImageFile()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    processAndCropImage(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    photoFile.delete()
                    _cameraState.value = CameraState.ERROR
                }
            }
        )
    }

    private fun processAndCropImage(originalFile: File) {
        try {
            val originalBitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
                ?: throw IllegalStateException("Failed to decode captured image")

            val rotatedBitmap = correctImageOrientation(originalBitmap, originalFile.absolutePath)

            val croppedBitmap = cropRelativeToOverlay(rotatedBitmap)

            val processedFile = createImageFile()
            saveProcessedImage(croppedBitmap, processedFile)

            originalFile.delete()
            originalBitmap.recycle()
            rotatedBitmap.recycle()
            croppedBitmap.recycle()

            _capturedImage.value = processedFile
            _cameraState.value = CameraState.PHOTO_CAPTURED

        } catch (e: Exception) {
            Log.e(TAG, "Failed to process captured image", e)
            originalFile.delete()
            _cameraState.value = CameraState.ERROR
        }
    }

    private fun correctImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            val isEmulator = Build.FINGERPRINT.contains("generic") || Build.MODEL.contains("Emulator")

            when {
                isEmulator -> {
                    Log.d(TAG, "Running on emulator, applying manual 90° rotation")
                    matrix.postRotate(90f)
                }
                orientation == ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                orientation == ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                orientation == ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to correct image orientation, using original", e)
            bitmap
        }
    }

    private fun saveProcessedImage(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { outputStream ->
            val success = bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            if (!success) {
                throw IllegalStateException("Failed to compress and save image")
            }
        }
    }

    override fun confirmPhoto(): File? {
        val capturedFile = _capturedImage.value
        return capturedFile
    }

    override fun retryPhoto() {
        _capturedImage.value?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        resetCameraState()
    }

    override fun startSaving() {
        _cameraState.value = CameraState.SAVING
    }

    override fun endSaving() {
        resetCameraState()
    }

    override fun resetCameraState() {
        _capturedImage.value = null
        _cameraState.value = CameraState.READY
    }

    override fun cleanup() {
        try {
            cameraProvider?.unbindAll()
            _capturedImage.value?.let { file ->
                if (file.exists()) {
                    file.delete()
                }
            }
            _capturedImage.value = null
            _cameraState.value = CameraState.IDLE
        } catch (e: Exception) {
            Log.e(TAG, "Error during camera cleanup", e)
        }
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_${timestamp}_${System.currentTimeMillis()}.jpg"
        return File(context.cacheDir, fileName)
    }
}

enum class CameraState {
    IDLE,
    READY,
    CAPTURING,
    PHOTO_CAPTURED,
    SAVING,
    ERROR
}
