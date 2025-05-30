package com.dambrofarne.eyeflush.data.repositories.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreDatabaseRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : DatabaseRepository {
    override fun addUser(uId: String, username: String) {
        val user = hashMapOf(
            "username" to username
        )
        db.collection("users")
            .document(uId)       // usa l'UID come ID documento
            .set(user)          // salva i dati nel documento con quell'ID
            .addOnSuccessListener {
                //Aggiunto con successo
                // Log.d(TAG, "DocumentSnapshot added with ID: $uId")
            }
            .addOnFailureListener { e ->
                //Fallimento
                // Log.w(TAG, "Error adding document", e)
            }
    }

    override fun addUser(uId: String) {
        db.collection("users")
            .document(uId)
            .set(emptyMap<String, Any>())
            .addOnSuccessListener {
                //Aggiunto con successo
                // Log.d(TAG, "DocumentSnapshot added with ID: $uId")
            }
            .addOnFailureListener { e ->
                //Fallimento
                // Log.w(TAG, "Error adding document", e)
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
}