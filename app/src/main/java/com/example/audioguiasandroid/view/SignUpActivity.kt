package com.example.audioguiasandroid.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.audioguiasandroid.model.data.ProviderType
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.signUp
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setup()
    }

    private fun setup(){
        title = "Registro"

        val nameEditText = findViewById<EditText>(R.id.nameEditText_SignUp)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText_SignUp)
        val emailEditText = findViewById<EditText>(R.id.emailEditText_SignUp)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText_SignUp)
        val password2EditText = findViewById<EditText>(R.id.password2EditText_SignUp)
        val signUpButton = findViewById<Button>(R.id.signUpButtton_SignUp)
        val backButton = findViewById<Button>(R.id.backButton_SignUp)


        signUpButton.setOnClickListener {
            if (signUp(this, emailEditText.text.toString(), passwordEditText.text.toString(), password2EditText.text.toString(), nameEditText.text.toString(), surnameEditText.text.toString(), ProviderType.BASIC.name)){
                showMain(this, "home")
            }
        }

        backButton.setOnClickListener {
            showAuth(this)
        }
    }
}