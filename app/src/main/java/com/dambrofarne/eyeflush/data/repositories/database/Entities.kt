package com.dambrofarne.eyeflush.data.repositories.database

import org.osmdroid.util.GeoPoint


data class Marker(
    val id : String,
    val name: String?,
    val coordinates: GeoPoint,
    val mostLikedPicId : String?,
    val mostLikedPicURL: String?
)

data class MarkerRaw(
    val id: String = "",
    val name: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val mostLikedPicId: String? = "",
    val mostLikedPicURL: String? = ""
) {
    fun toMarker() = Marker(
        id = id,
        name = name,
        coordinates = GeoPoint(latitude, longitude),
        mostLikedPicId = mostLikedPicId,
        mostLikedPicURL = mostLikedPicURL
    )
}