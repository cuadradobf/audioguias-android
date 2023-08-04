package com.example.audioguiasandroid.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.model.data.Comment
import com.example.audioguiasandroid.model.repository.CommentDiffUtil

class CommentsAdapter(private var listComments:List<Comment>, val onItemRemove:(Comment, String) -> Unit):RecyclerView.Adapter<CommentsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommentsViewHolder(layoutInflater.inflate(R.layout.item_comments, parent, false))
    }

    override fun getItemCount(): Int = listComments.size

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val item = listComments[position]
        holder.render(item, onItemRemove)
    }

    fun updateData(newData: List<Comment>){
        val commentDiff = CommentDiffUtil(listComments, newData)
        val result = DiffUtil.calculateDiff(commentDiff)
        listComments = newData
        result.dispatchUpdatesTo(this)
    }
}