package com.dambrofarne.eyeflush.data.repositories.database

import org.osmdroid.util.GeoPoint

interface DatabaseRepository {
    //Users
    suspend fun addUser( uId : String, username: String): Result<String>
    suspend fun addUser( uId : String): Result<String>
    suspend fun isUser( uId : String) : Boolean
    suspend fun changeProfileImage(uId : String, imagePath: String) : Result<String>
    suspend fun getUserImagePath(uId: String) : String
    suspend fun isUsernameTaken(username: String) : Boolean
    suspend fun getUsername(uId: String) : String
    //Markers
    suspend fun getMarkersInRange(point: GeoPoint, rangeMeters: Int) : List<Marker>;
    suspend fun getNearestMarker(point: GeoPoint, range: Int);
}