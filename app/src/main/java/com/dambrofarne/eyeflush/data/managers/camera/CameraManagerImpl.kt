package com.dambrofarne.eyeflush.data.managers.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImgurImageStoringRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

class CameraManagerImpl(
    private val context: Context
) : CameraManager {

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val imageStoring = ImgurImageStoringRepository(context)

    private val _cameraState = MutableStateFlow(CameraState.IDLE)
    override val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private val _capturedImage = MutableStateFlow<File?>(null)
    override val capturedImage: StateFlow<File?> = _capturedImage.asStateFlow()

    override fun initializeCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        executor: Executor
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                startCamera(previewView, lifecycleOwner)
                _cameraState.value = CameraState.READY
            } catch (exc: Exception) {
                Log.e("CameraManager", "Errore nell'inizializzazione della fotocamera", exc)
                _cameraState.value = CameraState.ERROR
            }
        }, executor)
    }

    private fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()

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
            Log.e("CameraManager", "Camera binding Error", exc)
            _cameraState.value = CameraState.ERROR
        }
    }

    override fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        if (_cameraState.value != CameraState.READY) return

        _cameraState.value = CameraState.CAPTURING

        val photoFile = createImageFile()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    _capturedImage.value = photoFile
                    _cameraState.value = CameraState.PHOTO_CAPTURED
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraManager", "Errore nella cattura della foto", exception)
                    _cameraState.value = CameraState.ERROR
                    photoFile.delete()
                }
            }
        )
    }

    override fun confirmPhoto(): File? {
        val photo = _capturedImage.value
        resetCameraState()
        return photo
    }

    override fun retryPhoto() {
        _capturedImage.value?.delete()
        resetCameraState()
    }

    override suspend fun savePhoto(file: File) {
        try {
            val result = imageStoring.uploadCroppedImage(file)

            //TODO

        } catch (e: Exception) {
            Log.e("CameraManager", "Errore durante l'upload dell'immagine", e)
            //TODO
        }
    }

    private fun associateMarker(location: GeoPoint) {

    }


    override fun resetCameraState() {
        _capturedImage.value = null
        _cameraState.value = CameraState.READY
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
            .format(System.currentTimeMillis())
        return File(context.cacheDir, "$timeStamp.jpg")
    }

    override fun cleanup() {
        cameraProvider?.unbindAll()
        _capturedImage.value?.delete()
    }
}

enum class CameraState {
    IDLE,
    READY,
    CAPTURING,
    PHOTO_CAPTURED,
    ERROR
}