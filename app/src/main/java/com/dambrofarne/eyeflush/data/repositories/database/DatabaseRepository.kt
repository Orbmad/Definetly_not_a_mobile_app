package com.dambrofarne.eyeflush.data.repositories.database

interface DatabaseRepository {
    suspend fun addUser( uId : String, username: String): Result<String>
    suspend fun addUser( uId : String): Result<String>
    suspend fun isUser( uId : String) : Boolean
    suspend fun changeProfileImage(uId : String, imagePath: String) : Result<String>
    suspend fun getUserImagePath(uId: String) : String
    suspend fun isUsernameTaken(username: String) : Boolean
}