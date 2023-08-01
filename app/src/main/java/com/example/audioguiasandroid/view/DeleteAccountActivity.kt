package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.showUserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class DeleteAccountActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleteaccount)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,"Alerta", "Los credenciales de tu cuenta se han perdido. Por favor, introducelos de nuevo.")
        }else{
            setup()
        }
    }

    private fun setup(){
        title = "Eliminar cuenta"
        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton_DeleteAccount)
        val backButton = findViewById<Button>(R.id.backButton_DeleteAccount)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText_DeleteAccount)

        deleteAccountButton.setOnClickListener {
            val credential = EmailAuthProvider.getCredential(Firebase.auth.currentUser?.email.toString(), passwordEditText.text.toString())

            Firebase.auth.currentUser!!.reauthenticate(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Log.d(ContentValues.TAG, "User re-authenticated.")

                        Firebase.auth.currentUser!!.delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(ContentValues.TAG, "User account deleted.")
                                }else{
                                    showAlert(this, "Error", "Error al eliminar su cuenta de autenticación.")
                                }
                            }
                        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).delete()
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.clear()
                        prefs.apply()

                        showAuth(this,"Información", "Su cuenta se ha eliminado con exito.")

                    }else{
                        showAlert(this, "Error", "Contraseña incorrecta.")
                    }
                }
        }

        backButton.setOnClickListener {
            showMain(this, "home")
        }

    }
}