package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.audioguiasandroid.HomeActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityAudioguideBinding
import com.example.audioguiasandroid.databinding.ActivityHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class AudioguideActitivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioguideBinding
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioguideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth("Alerta", "Los credenciales de tu cuenta se han perdido. Por favor, vuelve a iniciar sesión.")
        }else{
            val bundle = intent.extras
            val audioGuideID = bundle?.getString("audioGuide")

            if (audioGuideID != null){
                setup(audioGuideID)
            }

        }

        //Guardar datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", Firebase.auth.currentUser?.email.toString())
        prefs.apply()
    }

    private fun setup(audioGuideID: String){

        db.collection("audioGuide").document(audioGuideID).get()
            .addOnSuccessListener{
                binding.titleTextViewAudioGuideActivity.text = it.get("title") as String?
                binding.costTextViewAudioGuideActivity.text = it.get("cost").toString()
                binding.descriptionTextViewAudioGuideActivity.text = it.get("description") as String?
                val userID = it.get("user").toString()
                db.collection("user").document(userID).get()
                    .addOnSuccessListener {document ->
                        binding.autorTextViewAudioGuideActivity.text = document.get("name").toString() + " " + document.get("surname").toString()
                    }
                    .addOnFailureListener {e->
                        binding.autorTextViewAudioGuideActivity.text = "Anónimo"
                        Log.w(ContentValues.TAG, "Error getting user information.", e)
                    }
                val storage = Firebase.storage
                val audioGuideReference : StorageReference = storage.reference.child("images/audioGuides/" + it.id + "/main.jpg")
                audioGuideReference.downloadUrl
                    .addOnSuccessListener {uri ->
                    Picasso.get()
                        .load(uri)
                        .into(binding.mainImageViewAudioGuideActivity)
                    }
                    .addOnFailureListener {e ->
                        Log.w(ContentValues.TAG, "Error getting main image of audio guide.", e)
                    }
            }
            .addOnFailureListener {e ->
                Log.w(ContentValues.TAG, "Error firebase.", e)

            }

        binding.backButtonAudioGuideActivity.setOnClickListener{
            showHome()
        }
        //TODO: add ubicacion
        //TODO: add comentarios
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