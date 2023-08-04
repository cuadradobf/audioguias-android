package com.example.audioguiasandroid.model.repository


import android.text.Editable
import com.example.audioguiasandroid.model.data.AudioGuide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot

class AudioGuideRepository {
    fun getAllAudioGuides(result: QuerySnapshot): List<AudioGuide> {
        val listAudioGuide = mutableListOf<AudioGuide>()
        for (document in result) {
            listAudioGuide.add(
                AudioGuide(
                    document.id,
                    document.get("user").toString(),
                    document.getString("title") ?: "",
                    document.getDouble("cost") ?: 0.0,
                    document.getString("description") ?: "",
                    document.getGeoPoint("location") ?: GeoPoint(0.0, 0.0),
                    document.getString("city") ?: "",
                    document.getString("country") ?: ""
                )
            )
        }
        return listAudioGuide.toList()
    }
    fun getFilteredList(listAudioGuide: List<AudioGuide>, filter: Editable?): List<AudioGuide>{
        return listAudioGuide.filter { audioGuide ->
            val cityMatches = audioGuide.city?.lowercase()?.contains(filter.toString().lowercase()) ?: false
            val countryMatches = audioGuide.country?.lowercase()?.contains(filter.toString().lowercase()) ?: false
            val titleMatches = audioGuide.title?.lowercase()?.contains(filter.toString().lowercase()) ?: false

            cityMatches || countryMatches || titleMatches

        }
    }
}