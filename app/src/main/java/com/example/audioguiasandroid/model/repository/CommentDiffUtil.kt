package com.example.audioguiasandroid.model.repository

import androidx.recyclerview.widget.DiffUtil
import com.example.audioguiasandroid.model.data.Comment

class CommentDiffUtil(
    private val oldList: List<Comment>,
    private val newList: List<Comment>
):DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
        //TODO: En caso de implementar la edicion de comentarios modificar esto
    }
}