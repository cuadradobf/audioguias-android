package com.example.audioguiasandroid.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.audioguiasandroid.BaseActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityConfigurationBinding
import com.example.audioguiasandroid.viewmodel.changeUnitOfMeasurement
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showDeleteAccount
import com.example.audioguiasandroid.viewmodel.showMain
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class ConfigurationActivity : BaseActivity() {
    private lateinit var binding: ActivityConfigurationBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_configuration)
        language = Locale.getDefault().language
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,getString(R.string.information), getString(R.string.lost_credentials))
        }else {

            setup()
        }

    }

    private fun setup() {

        binding.languageTextViewConfiguration.text = language


        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                //binding.languageTextViewConfiguration.text = document.getString("language") ?: "ES"
                binding.unitOfMeasurementTextViewConfiguration.text = document.getString("unitOfMeasurement") ?: "Km"
                                /*
                                var redMode = document.getString("redMode") ?: "all"
                                if (redMode == "all"){
                                    redMode = getString(R.string.all_redMode)
                                }else if (redMode == "wifi"){
                                    redMode = getString(R.string.wifi_redMode)
                                }else{
                                    redMode = getString(R.string.offline_redMode)
                                }
                                //binding.redModeTextViewConfiguration.text = redMode


                                var storage = document.getString("storage") ?: "internal"
                                if (storage == "internal"){
                                    storage = getString(R.string.internal_storage)
                                }else{
                                    storage = getString(R.string.external_storage)
                                }
                                binding.storageTextViewConfiguration.text = storage
                                 */
            }
        binding.backButtonConfiguration.setOnClickListener {
            showMain(this, "home")
        }

        binding.languageTitleTextViewCofiguration.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            var checkedItem = 1

            if (language == "ES" || language == "es"){
                checkedItem = 0
            }

            builder.setSingleChoiceItems(arrayOf(getString(R.string.spanish), getString(R.string.english)),checkedItem){dialog, item ->
                if (item == 1){
                    updateLocale(Locale.ENGLISH)
                }else if (item == 0){
                    updateLocale(Locale("es", "ES"))
                }

            }

            builder.create()

            builder.setPositiveButton("Cancelar",null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


        binding.unitOfMeasurementTitleTextViewCofiguration.setOnClickListener {
            db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { document->
                    if (document.getString("unitOfMeasurement") == "Mi"){
                        changeUnitOfMeasurement(this, arrayOf("Km", "Mi"), 1, binding.unitOfMeasurementTextViewConfiguration)
                    }else{
                        changeUnitOfMeasurement(this, arrayOf("Km", "Mi"), 0, binding.unitOfMeasurementTextViewConfiguration)
                    }
                }
        }
        /*
        binding.redModeTitleTextViewCofiguration.setOnClickListener {
            db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { document->
                    if (document.getString("redMode") == "wifi"){
                        changeRedMode(this,  1, binding.redModeTextViewConfiguration)
                    }else if (document.getString("redMode") == "offline"){
                        changeRedMode(this, 2, binding.redModeTextViewConfiguration)
                    }else{
                        changeRedMode(this, 0, binding.redModeTextViewConfiguration)
                    }
                }
        }

         */
        binding.deleteAccountTitleTextViewConfiguration.setOnClickListener {
            showDeleteAccount(this)
        }
        /*
        binding.storageTitleTextViewConfiguration.setOnClickListener {
            db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { document->
                    if (document.getString("storage") == "external"){
                        changeDownloadStorage(this, arrayOf(getString(R.string.internal_storage), getString(R.string.external_storage)), 1, binding.storageTextViewConfiguration)
                    }else{
                        changeDownloadStorage(this, arrayOf(getString(R.string.internal_storage), getString(R.string.external_storage)), 0, binding.storageTextViewConfiguration)
                    }
                }
        }

         */

    }
}