package com.example.audioguiasandroid.model.data

import com.google.firebase.firestore.GeoPoint

data class AudioGuide(
    val id: String,
    val user: String,
    val title: String,
    val description: String,
    val location: GeoPoint,
    val city: String,
    val country: String) {}