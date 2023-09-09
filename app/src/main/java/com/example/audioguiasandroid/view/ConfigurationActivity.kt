package com.example.audioguiasandroid.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.audioguiasandroid.BaseActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityConfigurationBinding
import com.example.audioguiasandroid.viewmodel.changeUnitOfMeasurement
import com.example.audioguiasandroid.viewmodel.showAboutUs
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showDeleteAccount
import com.example.audioguiasandroid.viewmodel.showMain
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale

class ConfigurationActivity : BaseActivity() {
    private lateinit var binding: ActivityConfigurationBinding
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
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        var unitOfMeasurement = prefs.getString("unitOfMeasurement", null)

        if (unitOfMeasurement == null){
            val editor = prefs.edit()
            editor.putString("unitOfMeasurement", "Km")
            editor.apply()

            unitOfMeasurement = "Km"
        }
        binding.unitOfMeasurementTextViewConfiguration.text = unitOfMeasurement

        binding.backButtonConfiguration.setOnClickListener {
            showMain(this, "home")
        }

        binding.languageTitleTextViewCofiguration.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            var checkedItem = 1

            if (language == "ES" || language == "es"){
                checkedItem = 0
            }

            builder.setSingleChoiceItems(arrayOf(getString(R.string.spanish), getString(R.string.english)),checkedItem){_, item ->
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
            if(unitOfMeasurement == "Mi"){
                changeUnitOfMeasurement(this, 1, binding.unitOfMeasurementTextViewConfiguration)
            }else{
                changeUnitOfMeasurement(this, 0, binding.unitOfMeasurementTextViewConfiguration)
            }
        }

        binding.deleteAccountTitleTextViewConfiguration.setOnClickListener {
            showDeleteAccount(this)
        }

        binding.aboutUsTitleTextViewConfiguration.setOnClickListener {
            showAboutUs(this)
        }

    }
}