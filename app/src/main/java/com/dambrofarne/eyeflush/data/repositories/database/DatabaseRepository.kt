package com.dambrofarne.eyeflush.data.repositories.database

interface DatabaseRepository {
    fun addUser( uId : String, username: String)
    fun addUser( uId : String)
    suspend fun isUser( uId : String) : Boolean
    suspend fun changeProfileImage(uId : String, imagePath: String) : Result<String>
    suspend fun getUserImagePath(uId: String) : String
    suspend fun isUsernameTaken(username: String) : Boolean
}