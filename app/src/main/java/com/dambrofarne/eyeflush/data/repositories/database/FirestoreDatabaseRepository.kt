package com.dambrofarne.eyeflush.data.repositories.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreDatabaseRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : DatabaseRepository {
    override suspend fun addUser(uId: String, username: String): Result<String> {
        val user = hashMapOf(
            "username" to username
        )
        return try {
            db.collection("users")
                .document(uId)
                .set(user, SetOptions.merge())
                .await()
            Result.success("Username aggiornato correttamente.")
        } catch (e: Exception) {
            Log.e("Firestore", "Errore aggiornando username per $uId", e)
            Result.failure(e)
        }
    }


    override suspend fun addUser(uId: String): Result<String> {
        return try {
            db.collection("users")
                .document(uId)
                .set(emptyMap<String, Any>())
                .await()
            Result.success("Utente creato correttamente.")
        } catch (e: Exception) {
            Log.e("Firestore", "Errore creando documento utente per $uId", e)
            Result.failure(e)
        }
    }


    override suspend fun isUser(uId: String): Boolean {
        return try {
            val doc = db.collection("users").document(uId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun changeProfileImage(uId: String, imagePath: String): Result<String> {
        try {
            db.collection("users")
                .document(uId)
                .update("profileImagePath", imagePath)
                .await()
            return Result.success("");
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore aggiornando immagine profilo per $uId", e)
            return Result.success(e.message ?: "");
        }
    }

    override suspend fun getUserImagePath(uId: String): String {
        return try {
            val snapshot = db.collection("users")
                .document(uId)
                .get()
                .await()

            snapshot.getString("profileImagePath") ?: ""
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore recuperando immagine profilo per $uId", e)
            ""
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore controllando se username è già preso: $username", e)
            false // oppure true se vuoi bloccare in caso di errore
        }
    }

    override suspend fun getUsername(uId: String): String {
        return try {
            val snapshot = db.collection("users")
                .document(uId)
                .get()
                .await()

            snapshot.getString("username") ?: ""
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore recuperando username del profilo per $uId", e)
            ""
        }
    }
}