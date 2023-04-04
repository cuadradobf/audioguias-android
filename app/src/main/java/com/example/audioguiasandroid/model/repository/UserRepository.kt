package com.example.audioguiasandroid.model.repository


import com.example.audioguiasandroid.model.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")

    suspend fun addUser(user: User): Boolean{
        return suspendCoroutine { continuation ->
            userCollection.document(user.email)
                .set(user)
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { exception -> continuation.resume(false) }
        }
    }
    //TODO: crear funcion set, get, buscar, delete
}