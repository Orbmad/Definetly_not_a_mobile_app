package com.dambrofarne.eyeflush.data.managers.location

import org.osmdroid.util.GeoPoint

interface LocationManager {

    suspend fun getCurrentLocation(): GeoPoint?

    fun startLocationUpdates(onLocationUpdate: (GeoPoint) -> Unit)
}