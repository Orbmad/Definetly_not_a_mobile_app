package com.dambrofarne.eyeflush.data.repositories.auth

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit>
    fun signOut()
    fun isUserLoggedIn() : Boolean
}