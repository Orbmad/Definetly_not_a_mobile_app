package com.dambrofarne.eyeflush.ui.screens.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dambrofarne.eyeflush.data.managers.camera.CameraManager
import com.dambrofarne.eyeflush.data.managers.location.LocationManager
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImageStoringRepository
import com.dambrofarne.eyeflush.utils.findNearestMarkerInRadius
import kotlinx.coroutines.runBlocking
import org.osmdroid.util.GeoPoint
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId

class CameraViewModel (
    private val db : DatabaseRepository,
    private val auth: AuthRepository,
    private val imgRepo: ImageStoringRepository,
    private val locationManager: LocationManager,
    val cameraManager: CameraManager
) : ViewModel() {

    suspend fun savePhoto(file: File): Boolean {
        val location = locationManager.getCurrentLocation()
        if (location != null) {
            // Get user ID
            val userId = auth.getCurrentUserId()
            // Upload image and get url
            val imageUrl = runBlocking {
                imgRepo.uploadCroppedImage(file).getOrNull()
            }
            Log.w("Marker test", "uploaded imgURL: $imageUrl")
            // Associate marker and get marker ID
            val markerId = associateMarkerToPhoto(location)
            if ((imageUrl != null) && (markerId != null) && (userId != null)) {
                // Upload image on db
                runBlocking {
                    db.addImage(
                        markerId = markerId,
                        uId = userId,
                        timeStamp = LocalDateTime.now(ZoneId.systemDefault()),
                        imgURL = imageUrl
                    )
                }
                Log.w("Marker test", "Uploaded photo, associated to Marker")
                return true
            } else {
                return false
            }
        } else {
            Log.e("Marker test", "The Image was not uploaded")
            return false
        }
    }

    private fun associateMarkerToPhoto(photoLocation: GeoPoint?): String? {
        if (photoLocation != null) {
            val radiusRangeInMeters = 25 // Markers distance
            val markersList = runBlocking {
                db.getMarkersInRange(photoLocation, radiusRangeInMeters)
            }
            if (markersList.isEmpty()) {
                // Create new Marker
                val newMarkerId = createNewMarker(photoLocation)
                return newMarkerId
            } else {
                // Find Marker to associate
                val nearestMarker = findNearestMarkerInRadius(
                    markersList,
                    photoLocation,
                    radiusRangeInMeters.toDouble()
                )
                return nearestMarker?.id
            }
        } else {
            return null
        }
    }

    private fun createNewMarker(location: GeoPoint): String{
        val markerId = runBlocking {
            db.addMarker(location, null)
        }
        return markerId
    }
}