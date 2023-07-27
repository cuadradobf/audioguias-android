package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityAudioguideBinding
import com.example.audioguiasandroid.databinding.ActivityFavsBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.example.audioguiasandroid.viewmodel.onItemSelected
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showUserProfile
import com.example.audioguiasandroid.viewmodel.showVerify
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class FavsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavsBinding
    private var db = FirebaseFirestore.getInstance()
    private var storage = Firebase.storage
    private lateinit var audioGuideAdapter: AudioGuideAdapter
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null) {
            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,
                "Alerta",
                "Los credenciales de tu cuenta se han perdido. Por favor, vuelve a iniciar sesión."
            )
        } else {
            setup()
        }
    }

    private fun setup() {
        title = "Favoritos"
        storage.reference.child("images/" + Firebase.auth.currentUser?.email.toString() + "/profile").downloadUrl
            .addOnSuccessListener { uri->
                Picasso.get()
                    .load(uri)
                    .into(binding.userImageViewFavs)
            }
            .addOnFailureListener {
                storage.reference.child("images/default/profile.png").downloadUrl
                    .addOnSuccessListener { uri->
                        Picasso.get()
                            .load(uri)
                            .into(binding.userImageViewFavs)
                    }
            }


        initRecyclerView()

        binding.searchEditTextFavs.addTextChangedListener {filter ->
            //TODO: cambiar la lista por la de favs
            db.collection("audioGuide")
                .get()
                .addOnSuccessListener { result ->
                    val listAudioGuide : MutableList<AudioGuide> = AudioGuideRepository().getAllAudioGuides(result).toMutableList()
                    //Al ser Firebase una base de datos no relacional no se pueden realizar consultas complejas como LIKE y contratar un servicio externo como Algolia (permite realizar consultas complejas) tiene un costo
                    val resultList = listAudioGuide.filter { audioGuide ->
                        val cityMatches = audioGuide.city?.lowercase()?.contains(filter.toString().lowercase()) ?: false
                        val countryMatches = audioGuide.country?.lowercase()?.contains(filter.toString().lowercase()) ?: false
                        val titleMatches = audioGuide.title?.lowercase()?.contains(filter.toString().lowercase()) ?: false

                        cityMatches || countryMatches || titleMatches
                    }
                    audioGuideAdapter.updateData(resultList)

                }
        }

        binding.userImageViewFavs.setOnClickListener {
            if (Firebase.auth.currentUser?.isEmailVerified == true){
                showUserProfile(this)
            }else{
                showVerify(this)
            }
        }
    }
    private fun initRecyclerView(){
        //TODO: cambiar la lista por la de favs
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAudioGuide_Home)
        db.collection("audioGuide").get()
            .addOnSuccessListener { result ->
                val listAudioGuide : List<AudioGuide> = AudioGuideRepository().getAllAudioGuides(result)
                val manager = LinearLayoutManager(this)
                recyclerView.layoutManager = manager
                audioGuideAdapter = AudioGuideAdapter(listAudioGuide){ onItemSelected(this, it) }
                recyclerView.adapter = audioGuideAdapter
                Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                //audioGuideAdapter.updateData(listAudioGuide.toList())
            }
            .addOnFailureListener { e ->

                Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
            }
    }

}
