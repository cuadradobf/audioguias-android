package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityAudioplayerBinding
import com.example.audioguiasandroid.viewmodel.showAudioguide
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
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
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    
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
            showAuth(this, getString(R.string.information), getString(R.string.lost_credentials))
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
        player = ExoPlayer.Builder(this).build()
        playerView = binding.playerAudioPlayer
        db.collection("audioGuide").document(audioGuideID).get()
            .addOnSuccessListener { document->
                binding.titleTextViewAudioPlayer.text = document.getString("title") ?: ""
                binding.descriptionTextViewAudioPlayer.text = document.getString("description") ?: ""
                val cc = (document.getString("city") ?: "") + ", " + (document.getString("country") ?: "")
                binding.cityCountryTextViewAudioPlayer.text = cc
                val userEmail = document.get("user").toString()
                db.collection("user").document(userEmail).get()
                    .addOnSuccessListener { user->
                        val userName = user.getString("name") ?: getString(R.string.anonymous)
                        val userSurname = user.getString("surname") ?: ""
                        if (userSurname.isNotEmpty()){
                            val text = "$userName $userSurname"
                            binding.autorTextViewAudioPlayer.text = text
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
                val listFavAudioGuides = document.get("favAudioGuide") as? List<*>
                if ((listFavAudioGuides != null) && listFavAudioGuides.contains(audioGuideID)){
                    binding.bookmarkImageViewAudioPlayer.setImageResource(R.drawable.baseline_bookmark_24)
                }
            }

        val storage = Firebase.storage
        val imageReference : StorageReference = storage.reference.child("images/audioGuides/$audioGuideID/main.jpg")
        imageReference.downloadUrl
            .addOnSuccessListener {uri ->
                Picasso.get()
                    .load(uri)
                    .into(binding.mainImageViewAudioPlayer)
            }
            .addOnFailureListener {e ->
                Log.w(ContentValues.TAG, "Error getting main image from Firebase Storage.", e)
            }

        val audioReference : StorageReference = storage.reference.child("audios/audioGuides/$audioGuideID/audio.mp3")
        audioReference.downloadUrl
            .addOnSuccessListener { uri->
                val item = MediaItem.Builder()
                    .setUri(uri)
                    .setMediaId(audioGuideID)
                    .build()

                player.setMediaItem(item)
                player.repeatMode = Player.REPEAT_MODE_OFF
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
                    var listFavAudioGuides = document.get("favAudioGuide") as? List<*>
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
    }
}