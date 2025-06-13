package com.dambrofarne.eyeflush.utils

import com.dambrofarne.eyeflush.data.repositories.database.Marker
import com.dambrofarne.eyeflush.ui.screens.home.PolaroidMarker
import org.osmdroid.util.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun locationMetersDistance(center: GeoPoint, location: GeoPoint): Double {
    return center.distanceToAsDouble(location)
}

fun locationMetersDistance(
    centerLatitude: Double,
    centerLongitude: Double,
    locationLatitude: Double,
    locationLongitude: Double
) : Double {
    val centerPoint = GeoPoint(centerLatitude, centerLongitude)
    val locationPoint = GeoPoint(locationLatitude, locationLongitude)
    return centerPoint.distanceToAsDouble(locationPoint)
}

fun isLocationInRadius(
    centerPoint: GeoPoint,
    locationPoint: GeoPoint,
    radiusInMeters: Double
) : Boolean {
    return (locationMetersDistance(centerPoint, locationPoint) <= radiusInMeters)
}

fun isLocationInRadius(
    centerLatitude: Double,
    centerLongitude: Double,
    locationLatitude: Double,
    locationLongitude: Double,
    radiusInMeters: Double
) : Boolean {
    return (locationMetersDistance(centerLatitude, centerLongitude, locationLatitude, locationLongitude) <= radiusInMeters)
}

data class BoundingBox(val minLat: Double, val maxLat: Double, val minLng: Double, val maxLng: Double)

fun getBoundingBox(lat: Double, lng: Double, rangeMeters: Int): BoundingBox {
    val earthRadius = 6371000.0 // metri

    // Converti range in gradi latitudine
    val latDelta = Math.toDegrees(rangeMeters / earthRadius)

    // Converti range in gradi longitudine (dipende dalla latitudine)
    val lngDelta = Math.toDegrees(rangeMeters / (earthRadius * cos(Math.toRadians(lat))))

    val minLat = lat - latDelta
    val maxLat = lat + latDelta
    val minLng = lng - lngDelta
    val maxLng = lng + lngDelta

    return BoundingBox(minLat, maxLat, minLng, maxLng)
}

fun isWithinRange(p1: GeoPoint, p2: GeoPoint, rangeMeters: Int): Boolean {
    val earthRadius = 6371000.0

    val dLat = Math.toRadians(p2.latitude - p1.latitude)
    val dLon = Math.toRadians(p2.longitude - p1.longitude)

    val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(p1.latitude)) *
            cos(Math.toRadians(p2.latitude)) * sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = earthRadius * c
    return distance <= rangeMeters
}

fun findNearestMarkerInRadius(
    markersList: List<Marker>,
    location: GeoPoint,
    radiusInMeters: Double
) : Marker? {

    if (markersList.isEmpty()) return null

    var nearestMarker: Marker = markersList[0]
    var lowerDistance: Double = locationMetersDistance(nearestMarker.coordinates, location)
    var distance = lowerDistance
    var tempMarker: Marker
    var foundFlag = false

    markersList.forEach { marker ->
        tempMarker = marker
        if (isLocationInRadius(tempMarker.coordinates, location, radiusInMeters)) {
            distance = locationMetersDistance(tempMarker.coordinates, location)
            foundFlag = true
            if (distance < lowerDistance) {
                lowerDistance = distance
                nearestMarker = tempMarker
            }
        }
    }

    return if (foundFlag) {
        nearestMarker
    } else {
        null
    }
}

