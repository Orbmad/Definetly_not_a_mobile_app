package com.dambrofarne.eyeflush.data.repositories.database

interface DatabaseRepository {
    fun addUser( uId : String, username: String)
    suspend fun isUser( uId : String) : Boolean
    suspend fun changeProfileImage(uId : String, imagePath: String) : Result<String>
}