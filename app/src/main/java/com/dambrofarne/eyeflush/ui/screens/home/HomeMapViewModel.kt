package com.dambrofarne.eyeflush.ui.screens.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.managers.location.LocationManager
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.Marker
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImageStoringRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.util.GeoPoint

class HomeMapViewModel(
    private val db: DatabaseRepository,
    private val auth: AuthRepository,
    private val imgRepo: ImageStoringRepository,
    val locationManager: LocationManager
) : ViewModel() {

    private val _polaroidMarkers = MutableStateFlow<List<PolaroidMarker>>(emptyList())
    val polaroidMarkers: StateFlow<List<PolaroidMarker>> = _polaroidMarkers.asStateFlow()

    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    val currentLocation: StateFlow<GeoPoint?> = _currentLocation.asStateFlow()

    private val _locationUpdated = MutableStateFlow(false)
    val locationUpdated = _locationUpdated.asStateFlow()

    init {
        observeLocationUpdates()
    }

    fun checkNotifications(): Boolean {
        val uId = auth.getCurrentUserId()
        if (uId != null) {
            return runBlocking {
                db.hasUnreadNotifications(uId)
            }
        }
        return false
    }

    fun updateCurrentLocation(location: GeoPoint) {
        _currentLocation.value = location
        _locationUpdated.value = !_locationUpdated.value
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            currentLocation
                .filterNotNull()
                .sample(2000L) // throttle: max every 2 seconds
                .distinctUntilChanged { old, new ->
                    old.distanceToAsFloat(new) < 4f // equal if less
                }
                .flowOn(Dispatchers.Default)
                .collect { location ->
                    loadPolaroidMarkers(location)
                }
        }
    }

    private fun loadPolaroidMarkers(location: GeoPoint) {
        viewModelScope.launch {
            val photoDataList = db.getMarkersInRange(location, 1500)

            val markers = createPolaroidMarkersFromMarkers(photoDataList)
            _polaroidMarkers.value = markers
            //Log.w("test", "Markers: $markers")
        }
    }

//    fun createDummyMarkers() {
//        viewModelScope.launch {
//            val dummyMarkers = listOf(
//                PolaroidMarker(
//                    id = "1",
//                    position = GeoPoint(44.1482, 12.2356),
//                    photoFrame = createDummyDrawable(Color.RED)
//                ),
//                PolaroidMarker(
//                    id = "2",
//                    position = GeoPoint(44.1464, 12.2374),
//                    photoFrame = createDummyDrawable(Color.BLUE)
//                ),
//                PolaroidMarker(
//                    id = "3",
//                    position = GeoPoint(44.1480, 12.2330),
//                    photoFrame = createDummyDrawable(Color.GREEN)
//                )
//            )
//            _polaroidMarkers.value = dummyMarkers
//        }
//    }

    private suspend fun createPolaroidMarkersFromMarkers(markerList: List<Marker>): List<PolaroidMarker> {
        return markerList.map { marker ->
            PolaroidMarker(
                id = marker.id,
                position = marker.coordinates,
                photoFrame = createPhotoFrame(marker.mostLikedPicURL),
                photoCount = marker.imagesCount,
            )
        }
    }

    private suspend fun createPhotoFrame(uri: String?): Drawable {
        return if (uri != null) {
            imgRepo.getDrawableImage(Uri.parse(uri)).getOrElse {
                Log.e("ImageError", "Failed to load image for URI: $uri", it)
                createDummyDrawable(Color.BLUE)
            }
        } else {
            createDummyDrawable(Color.BLUE)
        }
    }

    private fun createDummyDrawable(color: Int): Drawable {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { this.color = color }
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        return BitmapDrawable(null, bitmap)
    }

    // Estensione di utilità per confronto geolocalizzazione
    private fun GeoPoint.distanceToAsFloat(other: GeoPoint): Double {
        return this.distanceToAsDouble(other)
    }
}
