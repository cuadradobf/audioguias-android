package com.example.audioguiasandroid.viewmodel

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.view.AudioguideActitivity
import com.example.audioguiasandroid.view.AudioplayerActivity
import com.example.audioguiasandroid.view.AuthActivity
import com.example.audioguiasandroid.view.ChangePasswordActivity
import com.example.audioguiasandroid.view.DeleteAccountActivity
import com.example.audioguiasandroid.view.MainActivity
import com.example.audioguiasandroid.view.SignUpActivity
import com.example.audioguiasandroid.view.UserProfileActivity
import com.example.audioguiasandroid.view.VerifyActivity

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

fun showMain(activity: AppCompatActivity, fragment: String){
    val intent = Intent(activity, MainActivity::class.java).apply {
        putExtra("fragment", fragment)
    }
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
fun showAuth(activity: AppCompatActivity){
    val intent = Intent(activity, AuthActivity::class.java)
    activity.startActivity(intent)
}
fun showVerify(activity: AppCompatActivity){
    val intent = Intent(activity, VerifyActivity::class.java)
    activity.startActivity(intent)
}

fun showAudioplayer(activity: AppCompatActivity, audioGuideID: String){
    val intent = Intent(activity, AudioplayerActivity::class.java).apply {
        putExtra("audioGuide", audioGuideID)
    }
    activity.startActivity(intent)
}

fun onItemSelected(activity: FragmentActivity, audioGuide: AudioGuide){
    val intent = Intent(activity, AudioguideActitivity::class.java).apply {
        putExtra("audioGuide", audioGuide.id)
    }
    activity.startActivity(intent)
}
fun showSignUp(activity: FragmentActivity){
    val intent = Intent(activity, SignUpActivity::class.java)
    activity.startActivity(intent)
}

fun showAudioguide(activity: FragmentActivity, audioGuideID: String) {
    val intent = Intent(activity, AudioguideActitivity::class.java).apply {
        putExtra("audioGuide", audioGuideID)
    }
    activity.startActivity(intent)
}

fun showChangePassword(activity: FragmentActivity){
    val intent = Intent(activity, ChangePasswordActivity::class.java)
    activity.startActivity(intent)
}

fun showDeleteAccount(activity: FragmentActivity){
    val intent = Intent(activity, DeleteAccountActivity::class.java)
    activity.startActivity(intent)
}



