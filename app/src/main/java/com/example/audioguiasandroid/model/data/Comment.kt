package com.example.audioguiasandroid.model.data

data class Comment (
    val id: String,
    val userID: String,
    val audioGuideID: String,
    val valoration: Double ?= null,
    val commentData: String ?= null
    ){}