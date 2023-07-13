package com.example.audioguiasandroid

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.databinding.ActivityHomeBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.AudioguideActitivity
import com.example.audioguiasandroid.view.AuthActivity
import com.example.audioguiasandroid.view.UserProfileActivity
import com.example.audioguiasandroid.view.VerifyActivity
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var audioGuideAdapter: AudioGuideAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.profileButtonHome.setOnClickListener {
            if (Firebase.auth.currentUser?.isEmailVerified == true){
                showUserProfile()
            }else{
                showVerify()
            }
        }

        initRecyclerView()

        //TODO: add filtro de busqueda
    }

    private fun initRecyclerView(){
        val db = FirebaseFirestore.getInstance()
        val audioGuideCollection = db.collection("audioGuide")
        val listAudioGuide = mutableListOf<AudioGuide>()


        audioGuideCollection
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    listAudioGuide.add(AudioGuide(
                        document.id,
                        document.get("title").toString(),
                        document.get("mainImage").toString(),
                        document.get("cost").toString().toDouble(),
                        document.get("description").toString(),
                        document.getGeoPoint("location"),
                        document.get("city").toString(),
                        document.get("country").toString()
                    ))
                }
                val manager = LinearLayoutManager(this)
                binding.recyclerAudioGuideHome.layoutManager = manager
                binding.recyclerAudioGuideHome.adapter = AudioGuideAdapter(listAudioGuide){ onItemSelected(it) }
                Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                //audioGuideAdapter.updateData(listAudioGuide.toList())
            }
            .addOnFailureListener { e ->

                Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
            }

    }

    /*
    private fun initRecyclerView(){
        val manager = LinearLayoutManager(this)
        //val decoration = DividerItemDecoration(this, manager.orientation)
        binding.recyclerAudioGuideHome.layoutManager = manager
        binding.recyclerAudioGuideHome.adapter = AudioGuideAdapter(
            listAudioGuide = listOf(
                AudioGuide("D4ssRwDYQ2zPwMqTJTOF", "Mezquita-Catedral", "gs://audioguias-24add.appspot.com/images/audioGuides/D4ssRwDYQ2zPwMqTJTOF/main.jpg", 0.0, "Descripcion", GeoPoint(37.723, -4.745), "Cordoba", "Spain"),
                AudioGuide("CCCgOaoasFvDiCOljTEO", "Medina Azahara", "gs://audioguias-24add.appspot.com/images/audioGuides/CCCgOaoasFvDiCOljTEO/main.jpg", 0.0, "Descripcion", GeoPoint(37.723, -4.745), "Cordoba", "Spain")
            )


        ) { onItemSelected(it) }

        //binding.recyclerAudioGuideHome.addItemDecoration(decoration)
    }

     */

    private fun onItemSelected(audioGuide: AudioGuide){
        //Toast.makeText(this, audioGuide.title, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AudioguideActitivity::class.java).apply {
            putExtra("audioGuide", audioGuide.id)
        }
        startActivity(intent)
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