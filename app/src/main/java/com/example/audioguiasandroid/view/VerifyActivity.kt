package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.audioguiasandroid.HomeActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.controller.sendEmailVerifyAccount
import com.example.audioguiasandroid.controller.showAlert
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth("Alerta", "Los credenciales de tu cuenta se han perdido. Por favor, introducelos de nuevo.")
        }else{
            setup()
        }
    }

    private fun setup(){
        title = "Verificación de cuenta"

        val verifyAccountButton = findViewById<Button>(R.id.verifyAccount_Verify)
        val backButton = findViewById<Button>(R.id.backButton_Verify)
        val logOutButton = findViewById<Button>(R.id.logOutButton_Verify)

        verifyAccountButton.setOnClickListener {
            sendEmailVerifyAccount(this)
        }

        logOutButton.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth("Información", "Se ha cerrado sesión.")
        }

        backButton.setOnClickListener {
            showHome()
        }
    }

    private fun showAuth(title: String, exception: String){
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra("title", title)
            putExtra("exception", exception)
        }
        startActivity(intent)
    }

    private fun showHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}

