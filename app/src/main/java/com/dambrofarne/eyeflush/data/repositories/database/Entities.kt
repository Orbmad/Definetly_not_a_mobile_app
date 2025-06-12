package com.dambrofarne.eyeflush.data.repositories.database

import com.google.firebase.Timestamp
import org.osmdroid.util.GeoPoint


data class Marker(
    val id : String,
    val name: String?,
    val coordinates: GeoPoint,
    val mostLikedPicId : String?,
    val mostLikedPicURL: String?,
    val imagesCount : Int
)

data class MarkerRaw(
    val id: String = "",
    val name: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val mostLikedPicId: String? = "",
    val mostLikedPicURL: String? = "",
    val imagesCount : Int = 0
) {
    fun toMarker() = Marker(
        id = id,
        name = name,
        coordinates = GeoPoint(latitude, longitude),
        mostLikedPicId = mostLikedPicId,
        mostLikedPicURL = mostLikedPicURL,
        imagesCount = imagesCount
    )
}

data class Picture(
    val id: String ="",
    val uId : String = "",
    val markerId : String = "",
    val url : String = "",
    val timeStamp : Timestamp = Timestamp.now(),
    val likes : Int = 0,
)