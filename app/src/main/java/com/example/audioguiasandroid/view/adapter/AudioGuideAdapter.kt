package com.example.audioguiasandroid.view.adapter

import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideDiffUtil

class AudioGuideAdapter(private var listAudioGuide:List<AudioGuide>, private val onClickListener: (AudioGuide, String) -> Unit) : RecyclerView.Adapter<AudioGuideViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioGuideViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AudioGuideViewHolder(layoutInflater.inflate(R.layout.item_audioguide, parent, false))
    }

    override fun getItemCount(): Int = listAudioGuide.size

    override fun onBindViewHolder(holder: AudioGuideViewHolder, position: Int) {
        val item = listAudioGuide[position]
        holder.render(item, onClickListener)
    }

    fun updateData(newData: List<AudioGuide>){
        val audioGuideDiff = AudioGuideDiffUtil(listAudioGuide, newData)
        val result = DiffUtil.calculateDiff(audioGuideDiff)
        listAudioGuide = newData
        result.dispatchUpdatesTo(this)
    }
}