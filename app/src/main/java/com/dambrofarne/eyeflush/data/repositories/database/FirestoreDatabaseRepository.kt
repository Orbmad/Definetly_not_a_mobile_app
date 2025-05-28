package com.dambrofarne.eyeflush.data.repositories.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

    override fun isUser(uId: String) {
        db.collection("users").document(uId).get()
            .addOnSuccessListener {
                //Utente esiste
            }
            .addOnFailureListener {
                //Utente non esiste
            }
    }
}