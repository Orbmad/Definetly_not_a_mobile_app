package com.dambrofarne.eyeflush.utils

import org.osmdroid.util.GeoPoint

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

