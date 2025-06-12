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
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.Marker
import com.dambrofarne.eyeflush.data.repositories.imagestoring.ImageStoringRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.util.GeoPoint

class HomeMapViewModel(
    private val db : DatabaseRepository,
    private val imgRepo: ImageStoringRepository,
    //private val auth : AuthRepository
) : ViewModel() {
    private val _polaroidMarkers = MutableStateFlow<List<PolaroidMarker>>(emptyList())
    val polaroidMarkers: StateFlow<List<PolaroidMarker>> = _polaroidMarkers.asStateFlow()

    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    val currentLocation: StateFlow<GeoPoint?> = _currentLocation.asStateFlow()

    fun updateCurrentLocation(location: GeoPoint) {
        _currentLocation.value = location
    }

    fun loadPolaroidMarkers() {
        val location = _currentLocation.value
        if (location != null) {
            val range = 1500 // place holder
            viewModelScope.launch {
                val photoDataList = db.getMarkersInRange(location, range)
                val markers = createPolaroidMarkersFromMarkers(photoDataList)
                _polaroidMarkers.value = markers
                Log.w("test", "Markers:$markers")
            }
        }
    }

    fun createDummyMarkers() {
        viewModelScope.launch {
            val dummyMarkers = listOf(
                PolaroidMarker(
                    id = "1",
                    position = GeoPoint(44.1482, 12.2356), // Università
                    photoFrame = createDummyDrawable(Color.RED)
                ),
                PolaroidMarker(
                    id = "2",
                    position = GeoPoint(44.1464, 12.2374), // Vicino coop
                    photoFrame = createDummyDrawable(Color.BLUE)
                ),
                PolaroidMarker(
                    id = "3",
                    position = GeoPoint(44.1480, 12.2330), // Ponte Nutrie
                    photoFrame = createDummyDrawable(Color.GREEN)
                )
            )

            _polaroidMarkers.value = dummyMarkers
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

    private fun createPolaroidMarkersFromMarkers(markerList: List<Marker>) : List<PolaroidMarker> {
        var polaroidMarkerList = mutableListOf<PolaroidMarker>()

        markerList.forEach { marker ->
            polaroidMarkerList.add(
                PolaroidMarker(
                    id = marker.id,
                    position = marker.coordinates,
                    photoFrame = createPhotoFrame(marker.mostLikedPicURL),
                    photoCount = marker.imagesCount,
                    name = marker.name,
                    mostLikedPicID = marker.mostLikedPicId
                )
            )
        }

        return polaroidMarkerList
    }

    private fun createPhotoFrame(uri: String?) : Drawable {
        return if (uri != null) {
            runBlocking {
                imgRepo.getDrawableImage(Uri.parse(uri)).getOrElse {
                    createDummyDrawable(Color.BLUE)
                }
            }
        } else {
            createDummyDrawable(Color.BLUE)
        }
    }
}