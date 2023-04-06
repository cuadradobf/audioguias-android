package com.example.audioguiasandroid.view

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.controller.showAlert
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

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
        title = "Cambio de contraseña"

        val actualPasswordEditText = findViewById<EditText>(R.id.actualPasswordEditText_ChagePassword)
        val newPasswordEditText = findViewById<EditText>(R.id.newPasswordEditText_ChagePassword)
        val newPasswordEditText2 = findViewById<EditText>(R.id.newPasswordEditText2_ChagePassword)
        val saveButton = findViewById<Button>(R.id.saveButton_ChangePassword)
        val backButton = findViewById<Button>(R.id.backButton_ChangePassword)


        saveButton.setOnClickListener {
            if (actualPasswordEditText.text.isNotEmpty() && newPasswordEditText.text.isNotEmpty() && newPasswordEditText2.text.isNotEmpty()){
                if (newPasswordEditText.text.toString() == newPasswordEditText2.text.toString()){

                    val credential = EmailAuthProvider.getCredential(Firebase.auth.currentUser?.email.toString(), actualPasswordEditText.text.toString())

                    Firebase.auth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(TAG, "User re-authenticated.")

                                Firebase.auth.currentUser!!.updatePassword(newPasswordEditText.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "User password updated.")
                                            showAlert(this, "Información", "Contraseña cambiada correctamente.")
                                        }else{
                                            showAlert(this, "Error", "Error al cambiar la contraseña.")
                                        }
                                    }

                            }else{
                                showAlert(this, "Error", "Contraseña actual incorrecta. Error al re-autenticar.")
                            }
                        }

                }else{
                    showAlert(this, "Error", "La nueva contraseña no coincide.")
                }
            }else{
                showAlert(this, "Error", "Campos obligatorios por completar.")
            }
        }

        backButton.setOnClickListener {
            showProfile()
        }
    }

    private fun showAuth(title: String, exception: String){
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra("title", title)
            putExtra("exception", exception)
        }
        startActivity(intent)
    }

    private fun showProfile(){
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }

}