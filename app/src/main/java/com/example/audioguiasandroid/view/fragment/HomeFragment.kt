package com.example.audioguiasandroid.view.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Context
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
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.example.audioguiasandroid.viewmodel.changeLocationMode
import com.example.audioguiasandroid.viewmodel.showAudioguide
import com.example.audioguiasandroid.viewmodel.updateDataAdapterByFilter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Locale

class HomeFragment : Fragment() {
    private var db = FirebaseFirestore.getInstance()
    private lateinit var audioGuideAdapter: AudioGuideAdapter
    private lateinit var listAudioGuide : List<AudioGuide>
    private var _binding: FragmentHomeBinding? = null
    private val language: String = Locale.getDefault().language
    private var flag: Boolean = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("locationMode", "off")
        editor.apply()

        setup()


        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setup() {
        val locationImageView = _binding?.locationImageViewHomeF
        val changeRangeLayout = _binding?.changeRangeLayoutHomeF
        val searchEditText = _binding?.searchEditTextHomeF

        initRecyclerView()
        requestLocationPermissions()
        initUserLocation()

        searchEditText?.addTextChangedListener {filter ->
            updateDataAdapterByFilter(audioGuideAdapter, listAudioGuide, filter)

            if (flag){
                val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString("locationMode", "off")
                prefs.apply()
                locationImageView?.setImageResource(R.drawable.location_grey)

                flag = false
            }


        }

        changeRangeLayout?.setOnClickListener {
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

                val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                val item = when(prefs.getString("locationMode", "off")){
                    "10" -> 0
                    "50" -> 1
                    else -> 2
                }
                val unitOfMeasurement = prefs.getString("unitOfMeasurement", "Km")
                val modes : Array<String>
                if (unitOfMeasurement == "Km"){
                    modes = arrayOf(getString(R.string.location_10km_mode),getString(R.string.location_50km_mode), getString(R.string.off_mode))
                }else{
                    modes = arrayOf(getString(R.string.location_6mi_mode),getString(R.string.location_30mi_mode), getString(R.string.off_mode))
                }
                flag = true
                changeLocationMode(requireActivity(), item, locationImageView, modes, R.drawable.location, R.drawable.location_grey, audioGuideAdapter)
            }

        }
    }

    private fun initUserLocation() {
        val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("locationMode", "off")
        prefs.apply()
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
                val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString("locationMode", "off")
                prefs.apply()
                _binding?.locationImageViewHomeF?.setImageResource(R.drawable.location_grey)
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
        if (language == "es"){
            db.collection("audioGuide")
                .whereEqualTo("language", "es")
                .get()
                .addOnSuccessListener { result ->
                    listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                    val manager = LinearLayoutManager(requireContext())
                    recyclerView?.layoutManager = manager
                    setAdapter()
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
                    setAdapter()
                    recyclerView?.adapter = audioGuideAdapter
                    Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
                }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario concedió el permiso
            } else {
                // El usuario denegó el permiso
                _binding?.locationImageViewHomeF?.setImageResource(R.drawable.location_grey)
                val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString("locationMode", "off")
                prefs.apply()
                Toast.makeText(requireContext(), getString(R.string.location_disabled), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setAdapter(){
        audioGuideAdapter = AudioGuideAdapter(listAudioGuide){ audioGuide, action ->
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
    }
}