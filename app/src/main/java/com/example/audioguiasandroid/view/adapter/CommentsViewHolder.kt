package com.example.audioguiasandroid.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.databinding.ItemCommentsBinding
import com.example.audioguiasandroid.model.data.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class CommentsViewHolder(view: View):RecyclerView.ViewHolder(view) {
    val binding = ItemCommentsBinding.bind(view)
    private var db = FirebaseFirestore.getInstance()
    private val storageReference = Firebase.storage.reference

    fun render(commentsModel: Comment){
        binding.valorationTextViewComments.text = commentsModel.valoration.toString() + "/5"
        binding.commentsTextViewComments.text = commentsModel.commentData.toString()
        db.collection("user").document(commentsModel.userID).get()
            .addOnSuccessListener { document->
                binding.autorTextViewComments.text = document.get("name").toString() + " " + document.get("surname").toString()
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
    }
}