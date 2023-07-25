package com.example.audioguiasandroid.model.repository


import android.content.ContentValues.TAG
import android.util.Log
import com.example.audioguiasandroid.model.data.ProviderType
import com.example.audioguiasandroid.model.data.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository {


    fun getUser(email: String): User{
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("user")
        var user: User = User(email)
        userCollection.document(email).get()
            .addOnSuccessListener {
                user.name = it.get("name") as String
                user.provider = it.get("provider") as String
                user.surname = it.get("surname") as String
                user.rol = it.get("rol") as String
                user.profileImage = it.get("profileImage") as String
                //TODO: Implementar audioguias en la base de datos
                //user.audioguiaList =
                Log.d(TAG, "User data got successfully.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting user data.", e)
            }
        return user
    }

    fun getAllUsers(): List<User> {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("user")
        val users = mutableListOf<User>()
        userCollection.get()
            .addOnSuccessListener { result ->
                val users = mutableListOf<User>()
                for (document in result) {
                    val user = User(document.get("email") as String)
                    user.name = document.get("name") as String
                    user.surname = document.get("surname") as String
                    user.rol = document.get("rol") as String
                    user.provider = document.get("provider") as String
                    user.profileImage = document.get("profileImage") as String
                    //TODO: Implementar audioguias
                    users.add(user)
                }
            }
        return users
    }

    fun setUser(user: User): Boolean {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("user")
        var result: Boolean = false

        userCollection.document(user.email).set(hashMapOf(
                "name" to user.name,
                "surname" to user.surname,
                "profileImage" to user.profileImage
                //TODO: Implementar audioguias
            ))
            .addOnSuccessListener {
                Log.d(TAG, "User data updated successfully.")
                result = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating user data.", e)
            }
        return result
    }

    fun deleteUser(email: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("user")
        var result: Boolean = false
        userCollection.document(email)
            .delete()
            .addOnSuccessListener { result = true }

        return result

    }
}