package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audioguiasandroid.BaseActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityAudioguideBinding
import com.example.audioguiasandroid.model.data.Comment
import com.example.audioguiasandroid.model.repository.CommentsRepository
import com.example.audioguiasandroid.view.adapter.CommentsAdapter
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showAudioplayer
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class AudioguideActitivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityAudioguideBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var map: GoogleMap
    private lateinit var commentsAdapter: CommentsAdapter
    private var storage = Firebase.storage

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

    private fun setup(audioGuideID: String){
        title = "Audioguias"
        //TODO: implementar app bar en el resto de vistas 
        db.collection("audioGuide").document(audioGuideID).get()
            .addOnSuccessListener{
                binding.titleTextViewAudioGuideActivity.text = it.get("title").toString()
                //binding.costTextViewAudioGuideActivity.text = it.get("cost").toString()
                binding.descriptionTextViewAudioGuideActivity.text = it.get("description").toString()
                val userID = it.get("user").toString()
                //Si el autor de la audioguia es el mismo que el usuario se oculta la opcion de comentar su propio contenido
                if (userID == Firebase.auth.currentUser?.email.toString()){
                    binding.ratingBarAudioGuideActivity.visibility = View.GONE
                    binding.commentLayoutAudioGuideActivity.visibility = View.GONE
                }else{
                    //Imagen de usuario que aparece al añadir un comentario
                    storage.reference.child("images/" + Firebase.auth.currentUser?.email.toString() + "/profile").downloadUrl
                        .addOnSuccessListener { uri->
                            Picasso.get()
                                .load(uri)
                                .into(binding.userImageViewAudioGuideActivity)
                        }
                        .addOnFailureListener {
                            storage.reference.child("images/default/profile.png").downloadUrl
                                .addOnSuccessListener { uri->
                                    Picasso.get()
                                        .load(uri)
                                        .into(binding.userImageViewAudioGuideActivity)
                                }
                        }
                    //Caso en el que el usuario ya ha realizado un comentario sobre la audioguia
                    db.collection("audioGuide").document(audioGuideID).collection("comments").document(Firebase.auth.currentUser?.email.toString()).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()){
                                val rating = document.get("valoration").toString().toFloat()
                                binding.ratingBarAudioGuideActivity.rating = rating
                                binding.commentEditTextAudioGuideActivity.hint = getString(R.string.modify_comment)
                            }
                        }
                }
                db.collection("user").document(userID).get()
                    .addOnSuccessListener {document ->
                        val name = document.getString("name") ?: getString(R.string.anonymous)
                        val surname = document.getString("surname") ?: ""
                        val text = "$name $surname"
                        binding.autorTextViewAudioGuideActivity.text = text
                    }
                    .addOnFailureListener {e->
                        binding.autorTextViewAudioGuideActivity.text = getString(R.string.anonymous)
                        Log.w(ContentValues.TAG, "Error getting user information.", e)
                    }

                storage.reference.child("images/audioGuides/" + it.id + "/main.jpg").downloadUrl
                    .addOnSuccessListener {uri ->
                        Picasso.get()
                            .load(uri)
                            .into(binding.mainImageViewAudioGuideActivity)
                    }
                    .addOnFailureListener {e ->
                        Log.w(ContentValues.TAG, "Error getting main image of audio guide.", e)
                        //Ocultamos ImageView
                        binding.mainImageViewAudioGuideActivity.visibility = View.GONE
                    }
            }
            .addOnFailureListener {e ->
                Log.w(ContentValues.TAG, "Error firebase.", e)

            }
        //Valoracion media de la audioguia
        db.collection("audioGuide").document(audioGuideID).collection("comments").get()
            .addOnSuccessListener { result ->
                var rating : Float = 0f
                for (document in result){
                    val valoration = document.getDouble("valoration") ?: 0.0
                    rating += valoration.toFloat()
                }
                rating /= result.size()
                binding.averageRatingBarAudioGuideActivity.rating = rating
                binding.commentsAmountTextViewAudioGuideActivity.text = result.size().toString()
            }
        //Comprueba si el usuario tiene guardada en su lista de favoritos la guia actual y cambia el icono del boton de guardado
        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                val listFavAudioGuides = document.get("favAudioGuide") as? List<*>
                if (listFavAudioGuides!= null && listFavAudioGuides.contains(audioGuideID)){
                    binding.bookmarkImageViewAudioGuideActivity.setImageResource(R.drawable.baseline_bookmark_24)
                }
                //Oculta la funcion de comentar si el usuario se encuentra baneado.
                val banned = document.getBoolean("banned") ?: false
                if (banned){
                    binding.ratingBarAudioGuideActivity.visibility = View.GONE
                    binding.commentLayoutAudioGuideActivity.visibility = View.GONE
                }
            }

        createGoogleMap()

        initRecyclerView(audioGuideID)

        //Boton favorito/guardado
        binding.bookmarkImageViewAudioGuideActivity.setOnClickListener {
            db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { document ->
                    var listFavAudioGuides = document.get("favAudioGuide") as? List<*>
                    if (listFavAudioGuides != null){
                        if (listFavAudioGuides.contains(audioGuideID)){
                            binding.bookmarkImageViewAudioGuideActivity.setImageResource(R.drawable.baseline_bookmark_border_24)
                            listFavAudioGuides = listFavAudioGuides.minus(audioGuideID)

                        }else{
                            binding.bookmarkImageViewAudioGuideActivity.setImageResource(R.drawable.baseline_bookmark_24)
                            listFavAudioGuides = listFavAudioGuides.plus(audioGuideID)
                        }
                    }else{
                        binding.bookmarkImageViewAudioGuideActivity.setImageResource(R.drawable.baseline_bookmark_24)
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

        binding.backButtonAudioGuideActivity.setOnClickListener{
            showMain(this, "home")
        }

        binding.playImageViewAudioGuideActivity.setOnClickListener {
            showAudioplayer(this, audioGuideID)
        }

        binding.sendImageViewAudioGuideActivity.setOnClickListener {
            sendComment(audioGuideID)
        }

    }

    private fun sendComment(audioGuideID: String) {
        val commentData = binding.commentEditTextAudioGuideActivity.text
        if (commentData.isNotEmpty() && commentData.isNotBlank() && binding.ratingBarAudioGuideActivity.rating.toDouble() != 0.0){
            db.collection("audioGuide").document(audioGuideID).collection("comments").document(Firebase.auth.currentUser?.email.toString()).set(
                hashMapOf(
                    "commentData" to commentData.toString(),
                    "user" to Firebase.auth.currentUser?.email.toString(),
                    "valoration" to binding.ratingBarAudioGuideActivity.rating.toDouble()
                )
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Comment data updated successfully.")
                //Oculta el teclado y las opciones de comentar
                val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val currentFocusView = this.currentFocus

                if (currentFocusView != null) {
                    inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
                }
                binding.ratingBarAudioGuideActivity.visibility = View.GONE
                binding.commentLayoutAudioGuideActivity.visibility = View.GONE
                db.collection("audioGuide").document(audioGuideID).collection("comments").get()
                    .addOnSuccessListener { result ->
                        val listComment = CommentsRepository().getAllComments(result, audioGuideID)
                        if(::commentsAdapter.isInitialized){
                            commentsAdapter.updateData(listComment)
                        }else{
                            val manager = LinearLayoutManager(this)
                            binding.commentsRecyclerAudioGuideActivity.layoutManager = manager
                            setAdapter(listComment, audioGuideID)
                            binding.commentsRecyclerAudioGuideActivity.adapter = commentsAdapter
                            binding.titleCommetsTextViewAudioGuideActivity.visibility = View.VISIBLE
                        }

                    }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating comment data.", e)
            }
        }else{
            showAlert(this,getString(R.string.information),getString(R.string.empty_comment))
        }
    }

    private fun initRecyclerView(audioGuideID: String) {
        //TODO: colocar el comentario del usuario logeado el primero
        db.collection("audioGuide").document(audioGuideID).collection("comments").get()
            .addOnSuccessListener { result ->
                val listComment = CommentsRepository().getAllComments(result, audioGuideID)

                if(listComment.size == 0){
                    binding.titleCommetsTextViewAudioGuideActivity.visibility = View.GONE
                }else{
                    val manager = LinearLayoutManager(this)
                    binding.commentsRecyclerAudioGuideActivity.layoutManager = manager
                    setAdapter(listComment, audioGuideID)
                    binding.commentsRecyclerAudioGuideActivity.adapter = commentsAdapter
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting comments data from Firebase Storage.", e)
            }

    }

    private fun createGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView_AudioGuideActivity) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Get the audio guide ID from the intent.
        val audioGuideID = intent.extras?.getString("audioGuide")

        // Add the marker to the map.
        if (audioGuideID != null){
            db.collection("audioGuide").document(audioGuideID).get().addOnSuccessListener { document ->
                val placeGeoPoint = document.get("location") as GeoPoint?
                if (placeGeoPoint != null){
                    val place = LatLng(placeGeoPoint.latitude, placeGeoPoint.longitude)
                    // Create a marker with the location of the audio guide.
                    val marker = map.addMarker(MarkerOptions().position(place))
                    if (marker != null) {
                        // Set the title of the marker to the name of the audio guide.
                        marker.title = document.get("title").toString()

                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(place, 16f),
                            1500,
                            null
                        )
                    }
                }
            }
        }
    }

    private fun setAdapter(listComment: List<Comment>, audioGuideID: String){
        commentsAdapter = CommentsAdapter(listComment){comment, action ->
            when(action){
                "delete" -> {
                    db.collection("audioGuide").document(audioGuideID).collection("comments").document(comment.id).delete()
                        .addOnSuccessListener {
                            val newList = listComment.minus(comment)
                            commentsAdapter.updateData(newList)
                        }
                }
                "block" -> {
                    db.collection("user").document(comment.id).set(
                        hashMapOf(
                            "banned" to true
                        ),
                        //Opcion para combinar los datos y que no los machaque
                        SetOptions.merge()
                    )
                    binding.ratingBarAudioGuideActivity.visibility = View.GONE
                    binding.commentLayoutAudioGuideActivity.visibility = View.GONE
                }
            }

        }
    }
}