package com.example.audioguiasandroid.view.adapter

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ItemAudioguideBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class AudioGuideViewHolder(view: View):RecyclerView.ViewHolder(view) {

    val binding = ItemAudioguideBinding.bind(view)
    private var db = FirebaseFirestore.getInstance()


    fun render(audioGuideModel: AudioGuide, onClickListener: (AudioGuide, String) -> Unit){
        binding.titleTextViewAudioGuide.text = audioGuideModel.title
        binding.descriptionTextViewAudioGuide.text = audioGuideModel.description
        //binding.costTextViewAudioGuide.text = audioGuideModel.cost.toString()
        val text = audioGuideModel.city + ", " + audioGuideModel.country
        binding.cityCountryTextViewAudioGuide.text = text
        setRating(audioGuideModel.id)

        val storage = Firebase.storage
        val storageRef = storage.reference
        val userRef: StorageReference = storageRef.child("images/audioGuides/" + audioGuideModel.id.toString() + "/main.jpg")

        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                if ( (document.getString("rol") ?: "Standar") == "Admin"){
                    binding.blockImageViewAudioGuide.visibility = View.VISIBLE
                    binding.deleteImageViewAudioGuide.visibility = View.VISIBLE
                }
            }

        //Descargar imagen de perfil
        userRef.downloadUrl.addOnSuccessListener {
            Picasso.get()
                .load(it)
                .into(binding.mainImageViewAudioGuide)
        }
        itemView.setOnClickListener {
            onClickListener(audioGuideModel, "view")
        }
        binding.deleteImageViewAudioGuide.setOnClickListener {
            onClickListener(audioGuideModel, "delete")
        }
        binding.blockImageViewAudioGuide.setOnClickListener {
            onClickListener(audioGuideModel, "block")        }
    }

    private fun setRating(id: String){
        db.collection("audioGuide").document(id).collection("comments").get()
            .addOnSuccessListener { result ->
                var rating : Float = 0f
                for (document in result){
                    val valoration = document.getDouble("valoration") ?: 0.0
                    rating += valoration.toFloat()
                }
                rating /= result.size()
                binding.ratingBarAudioGuide.rating = rating
                binding.commentsAmountTextViewAudioGuide.text = result.size().toString()
            }
    }
}