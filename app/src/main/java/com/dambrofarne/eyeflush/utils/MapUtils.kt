package com.dambrofarne.eyeflush.utils

import androidx.compose.foundation.pager.PagerSnapDistance
import com.dambrofarne.eyeflush.ui.screens.home.PolaroidMarker
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

fun findNearestMarkerInRadius(
    polaroidMarkersList: List<PolaroidMarker>,
    location: GeoPoint,
    radiusInMeters: Double
) : GeoPoint? {

    if (polaroidMarkersList.isEmpty()) return null

    var nearestGeoPoint: GeoPoint = polaroidMarkersList[0].getPosition()
    var lowerDistance: Double = locationMetersDistance(nearestGeoPoint, location)
    var distance = lowerDistance
    var markerPos: GeoPoint
    var foundFlag = false

    polaroidMarkersList.forEach { marker ->
        markerPos = marker.getPosition()
        if (isLocationInRadius(markerPos, location, radiusInMeters)) {
            distance = locationMetersDistance(markerPos, location)
            foundFlag = true
            if (distance < lowerDistance) {
                lowerDistance = distance
                nearestGeoPoint = markerPos
            }
        }
    }

    return if (foundFlag) {
        nearestGeoPoint
    } else {
        null
    }
}

