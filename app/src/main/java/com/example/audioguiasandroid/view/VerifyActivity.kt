package com.example.audioguiasandroid.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.sendEmailVerifyAccount
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.showVerify
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

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

    private fun setup(){
        title = getString(R.string.account_verification_title)

        val verifyAccountButton = findViewById<Button>(R.id.verifyAccount_Verify)
        val backButton = findViewById<Button>(R.id.backButton_Verify)
        val logOutButton = findViewById<Button>(R.id.logOutButton_Verify)

        verifyAccountButton.setOnClickListener {
            sendEmailVerifyAccount(this)
        }

        logOutButton.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this, getString(R.string.information), getString(R.string.log_out_info))
        }

        backButton.setOnClickListener {
            showMain(this, "home")
        }
    }

}

