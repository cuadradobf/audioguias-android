package com.example.audioguiasandroid.model.repository

import androidx.recyclerview.widget.DiffUtil
import com.example.audioguiasandroid.model.data.AudioGuide

class AudioGuideDiffUtil(
    private val oldList: List<AudioGuide>,
    private val newList: List<AudioGuide>
    ): DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}