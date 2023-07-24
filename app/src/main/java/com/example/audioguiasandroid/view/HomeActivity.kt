package com.example.audioguiasandroid

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audioguiasandroid.databinding.ActivityHomeBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.AudioguideActitivity
import com.example.audioguiasandroid.view.AuthActivity
import com.example.audioguiasandroid.view.UserProfileActivity
import com.example.audioguiasandroid.view.VerifyActivity
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var audioGuideAdapter: AudioGuideAdapter
    private var storage = Firebase.storage
    private var db = FirebaseFirestore.getInstance()
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

        storage.reference.child("images/" + Firebase.auth.currentUser?.email.toString() + "/profile").downloadUrl
            .addOnSuccessListener { uri->
                Picasso.get()
                    .load(uri)
                    .into(binding.userImageViewHome)
            }
            .addOnFailureListener {
                storage.reference.child("images/default/profile.png").downloadUrl
                    .addOnSuccessListener { uri->
                        Picasso.get()
                            .load(uri)
                            .into(binding.userImageViewHome)
                    }
            }
        initRecyclerView()

        binding.searchEditTextHome.addTextChangedListener {filter ->
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


        binding.userImageViewHome.setOnClickListener {
            if (Firebase.auth.currentUser?.isEmailVerified == true){
                showUserProfile()
            }else{
                showVerify()
            }
        }



    }

    fun initRecyclerView(){
        db.collection("audioGuide").get()
            .addOnSuccessListener { result ->
                val listAudioGuide : List<AudioGuide> = AudioGuideRepository().getAllAudioGuides(result)
                val manager = LinearLayoutManager(this)
                binding.recyclerAudioGuideHome.layoutManager = manager
                audioGuideAdapter = AudioGuideAdapter(listAudioGuide){ onItemSelected(it) }
                binding.recyclerAudioGuideHome.adapter = audioGuideAdapter
                Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                //audioGuideAdapter.updateData(listAudioGuide.toList())
            }
            .addOnFailureListener { e ->

                Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
            }
    }

    private fun initRV(){
        val listAudioGuide = mutableListOf<AudioGuide>()

        db.collection("audioGuide").get()
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