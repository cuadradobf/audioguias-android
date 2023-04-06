package com.example.audioguiasandroid

import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.audioguiasandroid.view.AuthActivity
import com.example.audioguiasandroid.view.UserProfileActivity
import com.example.audioguiasandroid.view.VerifyActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth("Alerta", "Los credenciales de tu cuenta se han perdido. Por favor, vuelve a iniciar sesión.")
        }else{
            setup()
        }

        //Guardar datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", Firebase.auth.currentUser?.email.toString())
        prefs.apply()

    }

    private fun setup(){
        title = "Inicio"

        val userProfileButton = findViewById<Button>(R.id.profileButton_Home)

        userProfileButton.setOnClickListener {
            showUserProfile()
            /*
            if (Firebase.auth.currentUser?.isEmailVerified == true){
                showUserProfile()
            }else{
                //TODO: ¿por qué no se envía el email de verificación?
                showVerify()

            }
            */

        }
    }

    private fun showAuth(title: String, exception: String){
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra("title", title)
            putExtra("exception", exception)
        }
        startActivity(intent)
    }

    private fun showUserProfile(){
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }

    private fun showVerify(){
        val intent = Intent(this, VerifyActivity::class.java)
        startActivity(intent)
    }
}