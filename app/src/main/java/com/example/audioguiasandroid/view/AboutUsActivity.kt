package com.example.audioguiasandroid.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityAboutUsBinding
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showContactUs
import com.example.audioguiasandroid.viewmodel.showMain
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_about_us)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,getString(R.string.information), getString(R.string.lost_credentials))
        }else{
            setup()
        }

    }

    private fun setup() {
        title = getString(R.string.about_us_title)

        binding.backButtonAboutUs.setOnClickListener {
            showMain(this,"home")
        }
        binding.contactUsButtonAboutUs.setOnClickListener {
            showContactUs(this)
        }
    }
}