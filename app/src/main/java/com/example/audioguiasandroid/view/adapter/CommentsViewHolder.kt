package com.example.audioguiasandroid.view.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.databinding.ItemCommentsBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.data.Comment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class CommentsViewHolder(view: View):RecyclerView.ViewHolder(view) {
    val binding = ItemCommentsBinding.bind(view)
    private var db = FirebaseFirestore.getInstance()
    private val storageReference = Firebase.storage.reference

    fun render(commentsModel: Comment, onItemRemove:(Comment, String) -> Unit){
        binding.ratingBarComments.rating = commentsModel.valoration?.toFloat() ?: 0f
        binding.commentsTextViewComments.text = commentsModel.commentData.toString()
        db.collection("user").document(commentsModel.userID).get()
            .addOnSuccessListener { document->
                val text = document.get("name").toString() + " " + document.get("surname").toString()
                binding.autorTextViewComments.text = text
            }
        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                if ( (document.getString("rol") ?: "Standar") == "Admin"){
                    binding.blockImageViewComments.visibility = View.VISIBLE
                    binding.deleteImageViewComments.visibility = View.VISIBLE
                }
            }
        storageReference.child("images/" + commentsModel.userID + "/profile").downloadUrl
            .addOnSuccessListener { uri->
                Picasso.get()
                    .load(uri)
                    .into(binding.userImageViewComments)
            }
            .addOnFailureListener {
                storageReference.child("images/default/profile.png").downloadUrl
                    .addOnSuccessListener { uri->
                        Picasso.get()
                            .load(uri)
                            .into(binding.userImageViewComments)
                    }
            }
        binding.deleteImageViewComments.setOnClickListener {
            onItemRemove(commentsModel, "delete")
        }
        binding.blockImageViewComments.setOnClickListener {
            onItemRemove(commentsModel, "block")
        }
    }
}