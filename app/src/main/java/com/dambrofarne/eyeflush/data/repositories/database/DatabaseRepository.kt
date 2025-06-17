package com.dambrofarne.eyeflush.data.repositories.database

import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

interface DatabaseRepository {
    //Users
    suspend fun addUser( uId : String, username: String): Result<String>
    suspend fun addUser( uId : String): Result<String>
    suspend fun isUser( uId : String) : Boolean
    suspend fun changeProfileImage(uId : String, imagePath: String) : Result<String>
    suspend fun getUserImagePath(uId: String) : String
    suspend fun isUsernameTaken(username: String) : Boolean
    suspend fun getUsername(uId: String) : String
    suspend fun getUserExtendedInfo(
        uId : String ,
        requesterUId: String)
    : Result<User>

    //Markers
    suspend fun getMarkersInRange(point: GeoPoint, rangeMeters: Int) : List<Marker>
    suspend fun addMarker(point: GeoPoint, name: String?) : String
    suspend fun getMarkerExtendedInfo(
        markerId : String,
        requesterUId: String) //The id of the user viewing the marker page, used to display its likes
    : Result<ExtendedMarker>

    //Pictures
    suspend fun addImage(markerId : String, uId : String, timeStamp : LocalDateTime, imgURL : String) : String
    suspend fun likeImage(uId: String, picId : String) : Result<Boolean>
    suspend fun hasUserLiked(uId: String, picId: String): Boolean
}