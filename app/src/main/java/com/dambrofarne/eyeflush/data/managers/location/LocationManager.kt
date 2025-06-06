package com.dambrofarne.eyeflush.data.managers.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager as AndroidLocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.osmdroid.util.GeoPoint
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager

    suspend fun getCurrentLocation(): GeoPoint? {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasLocationPermission()) {
                    return@withContext null
                }

                // Prima prova con l'ultima posizione conosciuta
                getLastKnownLocation()?.let { location ->
                    return@withContext GeoPoint(location.latitude, location.longitude)
                }

                // Se non c'è una posizione conosciuta, richiedi una nuova posizione
                requestCurrentLocation()?.let { location ->
                    GeoPoint(location.latitude, location.longitude)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onLocationUpdate: (GeoPoint) -> Unit) {
        val provider = AndroidLocationManager.GPS_PROVIDER
        if (hasLocationPermission() && locationManager.isProviderEnabled(provider)) {
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    onLocationUpdate(GeoPoint(location.latitude, location.longitude))
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            locationManager.requestLocationUpdates(provider, 2000L, 3f, listener)
        }
    }


    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastKnownLocation(): Location? {
        val providers = listOf(
            AndroidLocationManager.GPS_PROVIDER,
            AndroidLocationManager.NETWORK_PROVIDER,
            AndroidLocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null

        for (provider in providers) {
            if (locationManager.isProviderEnabled(provider)) {
                try {
                    val location = locationManager.getLastKnownLocation(provider)
                    if (location != null && isBetterLocation(location, bestLocation)) {
                        bestLocation = location
                    }
                } catch (e: SecurityException) {
                    println("Exception: LocationManager -> PermissionDenied")
                }
            }
        }

        return bestLocation
    }

    private suspend fun requestCurrentLocation(): Location? {
        return withTimeoutOrNull(10000L) { // Timeout di 10 secondi
            suspendCancellableCoroutine { continuation ->
                val providers = listOf(
                    AndroidLocationManager.GPS_PROVIDER,
                    AndroidLocationManager.NETWORK_PROVIDER
                )

                var resumed = false
                val listeners = mutableListOf<LocationListener>()

                for (provider in providers) {
                    if (locationManager.isProviderEnabled(provider)) {
                        try {
                            val listener = object : LocationListener {
                                override fun onLocationChanged(location: Location) {
                                    if (!resumed) {
                                        resumed = true
                                        // Rimuovi tutti i listener
                                        listeners.forEach { listener ->
                                            try {
                                                locationManager.removeUpdates(listener)
                                            } catch (e: Exception) {
                                                // TODO
                                            }
                                        }
                                        continuation.resume(location)
                                    }
                                }

                                @Deprecated("Deprecated in Java")
                                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                                override fun onProviderEnabled(provider: String) {}
                                override fun onProviderDisabled(provider: String) {}
                            }

                            listeners.add(listener)
                            locationManager.requestLocationUpdates(
                                provider,
                                1000L, // minTime
                                3f, // minDistance
                                listener
                            )
                        } catch (e: SecurityException) {
                            println("Exception: LocationManager -> SecurityException")
                        }
                    }
                }

                // Cleanup quando la coroutine viene cancellata
                continuation.invokeOnCancellation {
                    listeners.forEach { listener ->
                        try {
                            locationManager.removeUpdates(listener)
                        } catch (e: Exception) {
                            // TODO
                        }
                    }
                }

                // Se nessun provider è disponibile, ritorna null
                if (listeners.isEmpty()) {
                    continuation.resume(null)
                }
            }
        }
    }

    private fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            return true
        }

        // Controlla se la nuova posizione è più recente
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > 10 * 1000 // 10 secondi
        val isSignificantlyOlder = timeDelta < -10 * 1000
        val isNewer = timeDelta > 0

        if (isSignificantlyNewer) {
            return true
        } else if (isSignificantlyOlder) {
            return false
        }

        // Controlla l'accuratezza
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Determina se utilizzare la nuova posizione
        val isFromSameProvider = location.provider == currentBestLocation.provider

        return when {
            isMoreAccurate -> true
            isNewer && !isLessAccurate -> true
            isNewer && !isSignificantlyLessAccurate && isFromSameProvider -> true
            else -> false
        }
    }
}