package com.dambrofarne.eyeflush.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

data class PhotoMarker(
    val id: String,
    val position: GeoPoint,
    val photoUri: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HomeMapViewModel(
    private val db : DatabaseRepository,
    private val auth : AuthRepository
) : ViewModel() {
    private val _photoMarkers = MutableStateFlow<List<PhotoMarker>>(emptyList())
    val photoMarkers: StateFlow<List<PhotoMarker>> = _photoMarkers.asStateFlow()

    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    val currentLocation: StateFlow<GeoPoint?> = _currentLocation.asStateFlow()

    fun updateCurrentLocation(location: GeoPoint) {
        _currentLocation.value = location
    }

    fun addPhotoMarker(photoID: String, photoUri: String, location: GeoPoint) {
        viewModelScope.launch {
            val newMarker = PhotoMarker(
                id = photoID,
                position = location,
                photoUri = photoUri
            )
            _photoMarkers.value += newMarker
        }
    }
}