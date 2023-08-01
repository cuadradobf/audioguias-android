package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityAudioplayerBinding
import com.example.audioguiasandroid.model.repository.UserRepository
import com.example.audioguiasandroid.viewmodel.showAudioguide
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class AudioplayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioplayerBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerControlView

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this, "Alerta", "Los credenciales de tu cuenta se han perdido. Por favor, vuelve a iniciar sesión.")
        }else{
            val bundle = intent.extras
            val audioGuideID = bundle?.getString("audioGuide")

            if (audioGuideID != null){
                setup(audioGuideID)
            }else{
                showMain(this, "home")
            }
        }
    }

    private fun setup(audioGuideID: String) {
        title = "Reproductor de audio"
        player = SimpleExoPlayer.Builder(this).build()
        playerView = binding.playerAudioPlayer
        db.collection("audioGuide").document(audioGuideID).get()
            .addOnSuccessListener { document->
                binding.titleTextViewAudioPlayer.text = document.get("title").toString()
                val userEmail = document.get("user").toString()
                db.collection("user").document(userEmail).get()
                    .addOnSuccessListener { user->
                        val userName = user.get("name").toString()
                        val userSurname = user.get("surname").toString()
                        if (userSurname != null || userSurname != " "){
                            binding.autorTextViewAudioPlayer.text = userName + " " + userSurname
                        }else{
                            binding.autorTextViewAudioPlayer.text = userName
                        }
                    }
                    .addOnFailureListener { e->
                        Log.w(ContentValues.TAG, "Error getting user information from Firebase.", e)
                    }
            }
            .addOnFailureListener { e->
                Log.w(ContentValues.TAG, "Error getting audio guide information from Firebase.", e)
            }
        //Comprueba si el usuario tiene guardada en su lista de favoritos la guia actual y cambia el icono del boton de guardado
        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                val listFavAudioGuides = document.get("favAudioGuide") as? List<String>
                if (listFavAudioGuides!= null && listFavAudioGuides.contains(audioGuideID)){
                    binding.bookmarkImageViewAudioPlayer.setImageResource(R.drawable.baseline_bookmark_24)
                }
            }

        val storage = Firebase.storage
        val imageReference : StorageReference = storage.reference.child("images/audioGuides/" + audioGuideID + "/main.jpg")
        imageReference.downloadUrl
            .addOnSuccessListener {uri ->
                Picasso.get()
                    .load(uri)
                    .into(binding.mainImageViewAudioPlayer)
            }
            .addOnFailureListener {e ->
                Log.w(ContentValues.TAG, "Error getting main image from Firebase Storage.", e)
            }

        val audioReference : StorageReference = storage.reference.child("audios/audioGuides/" + audioGuideID + "/audio.mp3")
        audioReference.downloadUrl
            .addOnSuccessListener { uri->
                val item: MediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()
                player.addMediaItem(item)
                player.prepare()
                playerView.player = player
            }
            .addOnFailureListener { e->
                Log.w(ContentValues.TAG, "Error getting audio from Firebase Storage.", e)
            }

        binding.backButtonAudioPlayer.setOnClickListener {
            showAudioguide(this, audioGuideID)
        }

        //Boton favorito/guardado
        binding.bookmarkImageViewAudioPlayer.setOnClickListener {
            db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { document ->
                    var listFavAudioGuides = document.get("favAudioGuide") as? List<String>
                    if (listFavAudioGuides != null){
                        if (listFavAudioGuides.contains(audioGuideID)){
                            binding.bookmarkImageViewAudioPlayer.setImageResource(R.drawable.baseline_bookmark_border_24)
                            listFavAudioGuides = listFavAudioGuides.minus(audioGuideID)

                        }else{
                            binding.bookmarkImageViewAudioPlayer.setImageResource(R.drawable.baseline_bookmark_24)
                            listFavAudioGuides = listFavAudioGuides.plus(audioGuideID)
                        }
                    }else{
                        binding.bookmarkImageViewAudioPlayer.setImageResource(R.drawable.baseline_bookmark_24)
                        listFavAudioGuides = listOf(audioGuideID)
                    }
                    db.collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
                        hashMapOf(
                            "favAudioGuide" to listFavAudioGuides
                        ),
                        //Opcion para combinar los datos y que no los machaque
                        SetOptions.merge()
                    )
                }
        }
        //TODO: add boton para compartir. Tiene sentido? Solo puedes compartir para gente que tenga la aplicacion ya que no hay link a la pagina web
        //TODO: opcion para reproducir con el dispositivo bloqueado
    }
}