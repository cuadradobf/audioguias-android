package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.audioguiasandroid.HomeActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.controller.showAlert
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserProfileActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

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
        title = "Perfil"

        val userImage = findViewById<ImageView>(R.id.userImageView_UserProfile)
        val changeImageButton = findViewById<Button>(R.id.changeImageButton_UserProfile)
        val emailTextView = findViewById<TextView>(R.id.emailTextView_UserProfile)
        val nameEditText = findViewById<EditText>(R.id.nameEditText_UserProfile)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText_UserProfile)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton_UserProfile)
        val saveButton = findViewById<Button>(R.id.saveButton_UserProfile)
        val logOutButton = findViewById<Button>(R.id.logOutButton_UserProfile)
        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton_UserProfile)
        val backButton = findViewById<Button>(R.id.backButton_UserProfile)


        emailTextView.text = Firebase.auth.currentUser?.email.toString()
        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get().addOnSuccessListener {
            nameEditText.setText(it.get("name") as String?)
            surnameEditText.setText(it.get("surname") as String?)
        }

        changeImageButton.setOnClickListener {
            changeImage()
        }
        changePasswordButton.setOnClickListener {
            showChangePassword()
        }

        saveButton.setOnClickListener {
            //TODO: Comprobar que textos validos (sin numeros ni simbolos)
            if (nameEditText.text.isNotEmpty()){
                db.collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
                    hashMapOf("name" to nameEditText.text.toString(),
                        "surname" to surnameEditText.text.toString())
                )
                val user = Firebase.auth.currentUser

                //Actualizar el nombre en auth
                val profileUpdates = userProfileChangeRequest {
                    displayName = nameEditText.text.toString()
                }
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "User profile updated.")
                        }
                    }
            }else{
                showAlert(this,"Error","Campos obligatorios sin completar.")
            }


        }

        logOutButton.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            //TODO: Cambiar el tipo de alerta de Error a Anuncio o algo asi
            showAuth("Información", "Se ha cerrado sesión.")
        }

        deleteAccountButton.setOnClickListener {
            showDeleteAccount()
        }

        backButton.setOnClickListener {
            showHome()
        }

    }

    private fun changeImage(){
        //TODO:Cambiar imagen de perfil

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


    private fun showChangePassword(){
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun showDeleteAccount(){
        val intent = Intent(this, DeleteAccountActivity::class.java)
        startActivity(intent)
    }
}

