package com.example.simper

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreHelper(
    private val firestore: FirebaseFirestore,
    private val storageRef: StorageReference
) {

    companion object {
        private const val TAG = "FirestoreHelper"
    }

    suspend fun checkIfBookExists(collectionName: String, fileName: String, email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = firestore.collection(collectionName)
                    .whereEqualTo("fileName", fileName)
                    .whereEqualTo("email", email)
                    .get()
                    .await()
                !querySnapshot.isEmpty
            } catch (e: Exception) {
                Log.e(TAG, "Error checking if book exists", e)
                false
            }
        }
    }

    suspend fun removeBookFromCollection(collectionName: String, fileName: String, email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = firestore.collection(collectionName)
                    .whereEqualTo("fileName", fileName)
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        firestore.collection(collectionName).document(document.id).delete().await()
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing book from collection", e)
                false
            }
        }
    }

    fun addBookInfoToCollection(collectionName: String, fileName: String, email: String) {
        val bookInfo = hashMapOf(
            "email" to email,
            "fileName" to fileName,
            "uploadTime" to System.currentTimeMillis()
        )

        firestore.collection(collectionName)
            .add(bookInfo)
            .addOnSuccessListener {
                Log.i(TAG, "Book information added successfully to $collectionName")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding book information to $collectionName", e)
            }
    }


}
