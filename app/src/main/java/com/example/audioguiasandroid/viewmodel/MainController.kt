package com.example.audioguiasandroid.viewmodel

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.view.AudioguideActitivity
import com.example.audioguiasandroid.view.AudioplayerActivity
import com.example.audioguiasandroid.view.AuthActivity
import com.example.audioguiasandroid.view.FavsActivity
import com.example.audioguiasandroid.view.HomeActivity
import com.example.audioguiasandroid.view.UserProfileActivity
import com.example.audioguiasandroid.view.VerifyActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun showAlert(activity: AppCompatActivity, title: String, exception: String){
    val builder = AlertDialog.Builder(activity)

    if (title.isNullOrEmpty()){
        builder.setTitle("Error")
    }else{
        builder.setTitle(title)
    }

    if (exception.isNullOrEmpty()){
        builder.setMessage("Se ha producido un error.")
    }else{
        builder.setMessage(exception)
    }

    builder.setPositiveButton("Aceptar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

fun showHome(activity: AppCompatActivity){
    val intent = Intent(activity, HomeActivity::class.java)
    activity.startActivity(intent)
}
fun showUserProfile(activity: AppCompatActivity){
    val intent = Intent(activity, UserProfileActivity::class.java)
    activity.startActivity(intent)
}

fun showAuth(activity: AppCompatActivity, title: String, exception: String){
    val intent = Intent(activity, AuthActivity::class.java).apply {
        putExtra("title", title)
        putExtra("exception", exception)
    }
    activity.startActivity(intent)
}

fun showVerify(activity: AppCompatActivity){
    val intent = Intent(activity, VerifyActivity::class.java)
    activity.startActivity(intent)
}

fun showFavs(activity: AppCompatActivity){
    val intent = Intent(activity, FavsActivity::class.java)
    activity.startActivity(intent)
}

fun showAudioplayer(activity: AppCompatActivity){
    val intent = Intent(activity, AudioplayerActivity::class.java)
    activity.startActivity(intent)
}

fun onItemSelected(activity: AppCompatActivity, audioGuide: AudioGuide){
    val intent = Intent(activity, AudioguideActitivity::class.java).apply {
        putExtra("audioGuide", audioGuide.id)
    }
    activity.startActivity(intent)
}



