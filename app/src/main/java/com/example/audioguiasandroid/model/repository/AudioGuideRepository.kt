package com.example.audioguiasandroid.model.repository

import android.content.ContentValues
import android.util.Log
import com.example.audioguiasandroid.model.data.AudioGuide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.concurrent.CompletableFuture

class AudioGuideRepository {
    fun getAllAudioGuides(): CompletableFuture<List<AudioGuide>> {
        val db = FirebaseFirestore.getInstance()
        val audioGuideCollection = db.collection("audioGuide")
        val listAudioGuides = mutableListOf<AudioGuide>()

        val future = CompletableFuture<List<AudioGuide>>()

        audioGuideCollection.get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val audioGuide = AudioGuide(
                        document.id,
                        document.get("title") as String,
                        document.get("mainImage") as String,
                        document.get("cost") as Double,
                        document.get("description") as String,
                        document.get("location") as GeoPoint,
                        document.get("city") as String,
                        document.get("country") as String
                    )
                    listAudioGuides.add(audioGuide)
                }
                future.complete(listAudioGuides.toList())
                Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
            }
            .addOnFailureListener { e ->
                future.completeExceptionally(e)
                Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
            }

        return future
    }
}