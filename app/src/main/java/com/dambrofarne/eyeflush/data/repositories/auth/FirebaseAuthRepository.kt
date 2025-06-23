package com.dambrofarne.eyeflush.data.repositories.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
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

    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuth", "SUCCESS: User created")
                        Log.d("FirebaseAuth", "UID: ${auth.currentUser?.uid}")
                        cont.resume(Result.success(Unit))
                    } else {
                        Log.e("FirebaseAuth", "ERROR: ${task.exception?.message}")
                        cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
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
