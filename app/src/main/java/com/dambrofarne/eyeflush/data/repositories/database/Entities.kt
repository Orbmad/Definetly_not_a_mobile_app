package com.dambrofarne.eyeflush.data.repositories.database

import com.google.firebase.Timestamp
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime


data class Marker(
    val id : String,
    val name: String?,
    val coordinates: GeoPoint,
    val mostLikedPicId : String?,
    val mostLikedPicURL: String?,
    val mostLikedPicUserId : String ?,
    val mostLikedPicLikes : Int ?,
    val imagesCount : Int
)

data class MarkerRaw(
    val id: String = "",
    val name: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val mostLikedPicId: String? = "",
    val mostLikedPicURL: String? = "",
    val mostLikedPicUserId : String ? = "",
    val mostLikedPicLikes : Int ? = 0,
    val imagesCount : Int = 0
) {
    fun toMarker() = Marker(
        id = id,
        name = name,
        coordinates = GeoPoint(latitude, longitude),
        mostLikedPicId = mostLikedPicId,
        mostLikedPicURL = mostLikedPicURL,
        mostLikedPicUserId = mostLikedPicUserId,
        mostLikedPicLikes = mostLikedPicLikes,
        imagesCount = imagesCount
    )
}

data class PicQuickRef(
    val picId : String = "",
    val url : String = "",
    val liked : Boolean = false,
    val likes : Int = 0
)

data class ExtendedMarker(
    val id : String,
    val name: String?,
    val coordinates: GeoPoint,
    val mostLikedPicId : String?,
    val mostLikedPicURL: String?,
    val mostLikedPicUserId : String ?,
    val mostLikedPicLikes : Int ?,
    val mostLikedPicUserImage : String ?,
    val mostLikedPicUsername : String ?,
    val mostLikedPicTimeStamp : String ?,
    val imagesCount : Int,
    val picturesTaken : List<PicQuickRef>
)

data class Picture(
    val id: String ="",
    val uId : String = "",
    val markerId : String = "",
    val url : String = "",
    val timeStamp : Timestamp = Timestamp.now(),
    val likes : Int = 0,
    val authorUsername : String =  "",
    val authorImageUrl : String =  "",
    val markerName : String =  "",
)

data class PictureFormatted(
    val id: String ="",
    val uId : String = "",
    val markerId : String = "",
    val url : String = "",
    val timeStamp : String = "",
    val likes : Int = 0,
    val authorUsername : String =  "",
    val authorImageUrl : String =  "",
    val markerName : String =  "",
)


data class User (
    val uId : String,
    val username: String,
    val profileImagePath : String,
    val score: Int,
    val imagesCount: Int,
    val picturesTaken : List<PicQuickRef>
)

data class NotificationItem(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false,
    val referredMarkerId: String?,
)
