package com.example.audioguiasandroid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityResetPasswordBinding
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_reset_password)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup() {
        binding.backButtonResetPassword.setOnClickListener {
            showAuth(this)
        }
        binding.sendButtonResetPassword.setOnClickListener {
            val email = binding.emailEditTextResetPassword.text.toString()
            if (email.isNullOrEmpty()){
                showAlert(this, "Error", getString(R.string.invalid_email))
            }else{
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        showAuth(this,getString(R.string.information),getString(R.string.email_sent))
                    }
                    .addOnFailureListener { e ->
                        showAlert(this,"Error", e.message.toString())
                    }
            }
        }
    }
}