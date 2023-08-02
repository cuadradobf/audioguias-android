package com.example.audioguiasandroid.viewmodel

import android.text.Editable
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter


fun updateDataAdapterByFilter(audioGuideAdapter: AudioGuideAdapter, listAudioGuide: List<AudioGuide>, filter : Editable?){
    audioGuideAdapter.updateData(AudioGuideRepository().getFilteredList(listAudioGuide, filter))
}