package com.dambrofarne.eyeflush.ui.screens.camera

import androidx.lifecycle.ViewModel
import com.dambrofarne.eyeflush.data.managers.camera.CameraManager
import com.dambrofarne.eyeflush.data.managers.location.LocationManager
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImageStoringRepository

class CameraViewModel (
    private val db : DatabaseRepository,
    private val imgRepo: ImageStoringRepository,
    private val locationManager: LocationManager,
    private val cameraManager: CameraManager
) : ViewModel() {

}