package com.example.audioguiasandroid.view.adapter

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ItemAudioguideBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class AudioGuideViewHolder(view: View):RecyclerView.ViewHolder(view) {

    val binding = ItemAudioguideBinding.bind(view)


    fun render(audioGuideModel: AudioGuide, onClickListener: (AudioGuide) -> Unit){
        binding.titleTextViewAudioGuide.text = audioGuideModel.title
        binding.descriptionTextViewAudioGuide.text = audioGuideModel.description
        binding.costTextViewAudioGuide.text = audioGuideModel.cost.toString()
        //TODO: add ciudad y pais

        val storage = Firebase.storage
        val storageRef = storage.reference
        val userRef: StorageReference = storageRef.child("images/audioGuides/" + audioGuideModel.id.toString() + "/main.jpg")

        //Descargar imagen de perfil
        userRef.downloadUrl.addOnSuccessListener {
            Picasso.get()
                .load(it)
                .into(binding.mainImageViewAudioGuide)
        }
        itemView.setOnClickListener {
            onClickListener(audioGuideModel)
        }
    }
}