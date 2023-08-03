package com.example.audioguiasandroid.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityHelpBinding
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showContactUs
import com.example.audioguiasandroid.viewmodel.showMain
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_help)
        binding = ActivityHelpBinding.inflate(layoutInflater)
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
        title = getString(R.string.help_title)
        binding.backButtonHelp.setOnClickListener {
            showMain(this, "home")
        }
        binding.contactUsButtonHelp.setOnClickListener {
            showContactUs(this)
        }
        binding.webPageTextViewHelp.setOnClickListener {
            //TODO: link a la pagina web
        }
    }
}