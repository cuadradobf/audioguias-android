package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityContactUsBinding
import com.example.audioguiasandroid.databinding.ActivityHelpBinding
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showHelp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ContactUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactUsBinding
    private var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun setup() {
        title = getString(R.string.contact_us_title)

        binding.backButtonContactUs.setOnClickListener {
            showHelp(this)
        }
        binding.sendButtonHelp.setOnClickListener {
            val text = binding.feedbackEditTextViewContactUs.text.toString()
            if (text.isNullOrEmpty()){
                showAlert(this,"Error", "La caja de comentarios se encuentra vacía.")
            }else{
                db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                    .addOnSuccessListener {document ->
                        val name = document.getString("name") ?: getString(R.string.anonymous)
                        val surname = document.getString("surname") ?: ""

                        db.collection("question&feedback").add(
                            hashMapOf(
                                "name" to name,
                                "surname" to surname,
                                "email" to Firebase.auth.currentUser?.email.toString(),
                                "text" to text
                            )
                        )
                        Toast.makeText(this, "El comentario se ha enviado correctamente.", Toast.LENGTH_SHORT).show()
                        showHelp(this)
                    }
                    .addOnFailureListener { e->
                        Log.w(ContentValues.TAG, "Error getting user information.", e)
                    }
            }

        }

    }
}