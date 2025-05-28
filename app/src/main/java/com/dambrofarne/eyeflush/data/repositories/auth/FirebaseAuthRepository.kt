package com.dambrofarne.eyeflush.data.repositories.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseAuthRepository(private val auth: FirebaseAuth) : AuthRepository {
    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) cont.resume(Result.success(Unit))
                    else cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return suspendCancellableCoroutine { cont ->
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) cont.resume(Result.success(Unit))
                else cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) cont.resume(Result.success(Unit))
                    else cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                }
        }

    override fun signOut() {
        auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
