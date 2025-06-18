package com.dambrofarne.eyeflush.data.repositories.database

import com.google.firebase.Timestamp
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

    suspend fun getPictureExtendedInfo(picId : String) : Result<PictureFormatted>

    /**
     * @return A result containing an error message if
     *         it fails, the notification id it fails.
     */
    suspend fun addNotification(
        receiverUId : String,
        title : String,
        type : String,
        message : String,
        isRead : Boolean = false,
        markerId: String?
    ) : Result<String>

    /**
     * @return A list of the users notifications
     */
    suspend fun getNotifications(uId: String) : Unit

    /**
     * @param uId
     * @param notificationId
     *
     * @return True if the notification is now marked as red
     */
    suspend fun readNotification(uId : String, notificationId: String) : Result<Boolean>
}