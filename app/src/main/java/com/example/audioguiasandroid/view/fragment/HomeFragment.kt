package com.example.audioguiasandroid.view.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.FragmentHomeBinding
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.UserProfileActivity
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.example.audioguiasandroid.viewmodel.changeLocationMode
import com.example.audioguiasandroid.viewmodel.onItemSelected
import com.example.audioguiasandroid.viewmodel.updateDataAdapterByFilter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.Locale

class HomeFragment : Fragment() {
    private var storage = Firebase.storage
    private var db = FirebaseFirestore.getInstance()
    private lateinit var audioGuideAdapter: AudioGuideAdapter
    private lateinit var listAudioGuide : List<AudioGuide>
    private var _binding: FragmentHomeBinding? = null
    private lateinit var language: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    fun setListAudioGuide(newList: List<AudioGuide>) {
        listAudioGuide = newList
    }
    fun getLanguage():String{
        return language
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val currentLocale: Locale = resources.configuration.locale
        language = currentLocale.language
        setup()

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setup() {
        val locationImageView = _binding?.locationImageViewHomeF
        val userImageView = _binding?.userImageViewHomeF
        val searchEditText = _binding?.searchEditTextHomeF
        //Add funcion de ubicacion
        storage.reference.child("images/" + Firebase.auth.currentUser?.email.toString() + "/profile").downloadUrl
            .addOnSuccessListener { uri->
                Picasso.get()
                    .load(uri)
                    .into(userImageView)
            }
            .addOnFailureListener {
                storage.reference.child("images/default/profile.png").downloadUrl
                    .addOnSuccessListener { uri->
                        Picasso.get()
                            .load(uri)
                            .into(userImageView)
                    }
            }

        initRecyclerView()
        requestLocationPermissions()
        initUserLocation()

        searchEditText?.addTextChangedListener {filter ->
            updateDataAdapterByFilter(audioGuideAdapter, listAudioGuide, filter)
        }

        userImageView?.setOnClickListener {
            val intent = Intent(requireActivity(), UserProfileActivity::class.java)
            startActivity(intent)
        }

        locationImageView?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), getString(R.string.location_disabled), Toast.LENGTH_LONG).show()
            }else{
                db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                    .addOnSuccessListener { document ->
                        val item = when(document.getString("locationMode") ?: "off"){
                            "50" -> 1
                            "10" -> 0
                            else -> 2
                        }
                        changeLocationMode(requireActivity(), item, _binding?.locationImageViewHomeF, R.drawable.baseline_location_on_24, R.drawable.baseline_location_off_24, audioGuideAdapter)

                    }
            }

        }
    }

    private fun initUserLocation() {
        FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
            hashMapOf(
                "locationMode" to "off"
            ),
            //Opcion para combinar los datos y que no los machaque
            SetOptions.merge()
        )
    }

    private fun requestLocationPermissions() {
        val locationPermissionRequest = this.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
                FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
                    hashMapOf(
                        "locationMode" to "off"
                    ),
                    //Opcion para combinar los datos y que no los machaque
                    SetOptions.merge()
                )
                _binding?.locationImageViewHomeF?.setImageResource(R.drawable.baseline_location_off_24)
            }
            }
        }

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    private fun initRecyclerView(){
        val recyclerView : RecyclerView? = _binding?.recyclerAudioGuideHomeF
        if (recyclerView != null){
            if (language == "es"){
                db.collection("audioGuide")
                    .whereEqualTo("language", "es")
                    .get()
                    .addOnSuccessListener { result ->
                        listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                        val manager = LinearLayoutManager(requireContext())
                        recyclerView?.layoutManager = manager
                        audioGuideAdapter = AudioGuideAdapter(listAudioGuide){ onItemSelected(requireActivity(), it) }
                        recyclerView?.adapter = audioGuideAdapter
                        Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
                    }
            }else{
                db.collection("audioGuide")
                    .whereEqualTo("language", "en")
                    .get()
                    .addOnSuccessListener { result ->
                        listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                        val manager = LinearLayoutManager(requireContext())
                        recyclerView?.layoutManager = manager
                        audioGuideAdapter = AudioGuideAdapter(listAudioGuide){ onItemSelected(requireActivity(), it) }
                        recyclerView?.adapter = audioGuideAdapter
                        Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
                    }
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario concedió el permiso, obtén la ubicación actual
                changeLocationMode(requireActivity(), 0, _binding?.locationImageViewHomeF, R.drawable.baseline_location_on_24, R.drawable.baseline_location_off_24, audioGuideAdapter)
            } else {
                // El usuario denegó el permiso, toma medidas apropiadas (por ejemplo, mostrar un mensaje)
                _binding?.locationImageViewHomeF?.setImageResource(R.drawable.baseline_location_off_24)
                db.collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
                    hashMapOf(
                        "locationMode" to "off"
                    ),
                    //Opcion para combinar los datos y que no los machaque
                    SetOptions.merge()
                )
                Toast.makeText(requireContext(), getString(R.string.location_disabled), Toast.LENGTH_LONG).show()
            }
        }
    }
}