package com.example.audioguiasandroid.model.data

import com.google.firebase.firestore.GeoPoint

data class AudioGuide(
    val id: String,
    val user: String,
    val title: String ?= null,
    val cost: Double ?= null,
    val description: String ?= null,
    val location: GeoPoint ?= null,
    val city: String ?= null,
    val country: String ?= null) {}