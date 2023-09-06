package com.example.audioguiasandroid.view.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.databinding.FragmentFavsBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.example.audioguiasandroid.viewmodel.showAudioguide
import com.example.audioguiasandroid.viewmodel.updateDataAdapterByFilter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class FavsFragment : Fragment() {
    private var db = FirebaseFirestore.getInstance()
    private lateinit var audioGuideAdapter: AudioGuideAdapter
    private var _binding: FragmentFavsBinding? = null
    private lateinit var listAudioGuide : List<AudioGuide>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setup()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setup() {
        val searchEditText = _binding?.searchEditTextFavs

        initRecyclerView()

        searchEditText?.addTextChangedListener {filter ->
            updateDataAdapterByFilter(audioGuideAdapter, listAudioGuide, filter)
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView? = _binding?.recyclerAudioGuideFavs
        if (recyclerView != null) {
            db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { document ->
                    val listFavAudioGuides = document.get("favAudioGuide") as? List<*>
                    if (listFavAudioGuides != null && listFavAudioGuides.isNotEmpty()) {
                        db.collection("audioGuide")
                            .whereIn(FieldPath.documentId(), listFavAudioGuides)
                            .get()
                            .addOnSuccessListener { result ->
                                listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                                val manager = LinearLayoutManager(requireContext())
                                recyclerView.layoutManager = manager
                                audioGuideAdapter = AudioGuideAdapter(listAudioGuide) {audioGuide, action ->
                                    when(action){
                                        "view" -> showAudioguide(requireActivity(), audioGuide.id)
                                        "delete" -> {
                                            db.collection("audioGuide").document(audioGuide.id).delete()
                                                .addOnSuccessListener {
                                                    listAudioGuide = listAudioGuide.minus(audioGuide)
                                                    audioGuideAdapter.updateData(listAudioGuide)
                                                }
                                        }
                                        "block" -> {
                                            db.collection("user").document(audioGuide.user).set(
                                                hashMapOf(
                                                    "banned" to true
                                                ),
                                                //Opcion para combinar los datos y que no los machaque
                                                SetOptions.merge()
                                            )
                                        }
                                    }
                                }
                                recyclerView.adapter = audioGuideAdapter
                                Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                                //audioGuideAdapter.updateData(listAudioGuide.toList())
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error getting user data.", e)
                }
        }
    }
}