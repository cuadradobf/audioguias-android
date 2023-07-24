package com.example.audioguiasandroid.model.repository

import com.example.audioguiasandroid.model.data.Comment
import com.google.firebase.firestore.QuerySnapshot

class CommentsRepository {
    fun getAllComments(result: QuerySnapshot, audioGuideID: String) : List<Comment>{
        val listComments = mutableListOf<Comment>()
        for (document in result) {
            listComments.add(
                Comment(
                    document.id,
                    document.get("user").toString(),
                    audioGuideID,
                    document.get("valoration").toString().toDouble(),
                    document.get("commentData").toString()
                )
            )
        }
        return listComments.toList()
    }
}