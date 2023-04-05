package com.example.audioguiasandroid.model.repository


import com.example.audioguiasandroid.model.data.ProviderType
import com.example.audioguiasandroid.model.data.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")

    suspend fun getUser(email: String): User{
        return suspendCoroutine { continuation ->
            userCollection.document(email)
                .get()
                .addOnSuccessListener {
                    val user: User = User(it.get("email") as String, it.get("name") as String, it.get("provider") as ProviderType)
                    user.surname = it.get("surname") as String
                    user.rol = it.get("rol") as String
                    user.profileImage = it.get("profileImage") as String
                    //TODO: Implementar audioguias en la base de datos
                    //user.audioguiaList =
                    continuation.resume(user)
                }
                .addOnFailureListener{
                    val user: User = User("","",ProviderType.BASIC)
                    continuation.resume(user)
                }
        }
    }

    suspend fun getAllUsers(): List<User> {
        return suspendCoroutine { continuation ->
            userCollection.get()
                .addOnSuccessListener { result ->
                    val users = mutableListOf<User>()
                    for (document in result) {
                        val user: User = User(document.get("email") as String, document.get("name") as String, document.get("provider") as ProviderType)
                        user.surname = document.get("surname") as String
                        user.rol = document.get("rol") as String
                        user.profileImage = document.get("profileImage") as String
                        //TODO: Implementar audioguias
                        users.add(user)
                    }
                    continuation.resume(users)
                }
                .addOnFailureListener { exception ->
                    continuation.resume(emptyList())
                }
        }
    }

    suspend fun setUser(user: User): Boolean {
        return suspendCoroutine { continuation ->
            userCollection.document(user.email)
                .set(user)
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { exception -> continuation.resume(false) }
        }
    }

    suspend fun deleteUser(email: String): Boolean {
        return suspendCoroutine { continuation ->
            userCollection.document(email)
                .delete()
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { exception -> continuation.resume(false) }
        }
    }
}