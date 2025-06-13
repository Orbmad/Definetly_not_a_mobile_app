package com.dambrofarne.eyeflush.ui.screens.camera

import androidx.lifecycle.ViewModel
import com.dambrofarne.eyeflush.data.managers.camera.CameraManager
import com.dambrofarne.eyeflush.data.managers.location.LocationManager
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImageStoringRepository
import java.io.File

class CameraViewModel (
    private val db : DatabaseRepository,
    private val imgRepo: ImageStoringRepository,
    val locationManager: LocationManager,
    val cameraManager: CameraManager
) : ViewModel() {

    suspend fun savePhoto(file: File) {
        //TODO
    }
}