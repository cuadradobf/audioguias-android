package com.example.audioguiasandroid.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.showSignUp
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val bundle = intent.extras
        val title = bundle?.getString("title")
        val exception = bundle?.getString("exception")

        if (exception.isNullOrEmpty() || title.isNullOrEmpty()){
            setup()
        }else{
            showAlert(this, title, exception)
            setup()
        }

        session()

    }

    override fun onStart(){
        super.onStart()
        val authLayout = findViewById<ConstraintLayout>(R.id.authLayout_Auth)
        authLayout.visibility = View.VISIBLE
    }
    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email != null){
            val authLayout = findViewById<ConstraintLayout>(R.id.authLayout_Auth)
            authLayout.visibility = View.INVISIBLE
            showMain(this, "home")
        }
    }

    private fun setup(){
        title = "Acceso"

        val emailEditText = findViewById<EditText>(R.id.emailEditText_Auth)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText_Auth)
        val signUpButton = findViewById<Button>(R.id.signUpButton_Auth)
        val signInButton = findViewById<Button>(R.id.signInButton_Auth)

        signInButton.setOnClickListener {
            if(emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showMain(this, "home")
                    }else{
                        showAlert(this, "Error", it.exception?.message.toString())
                    }
                }
            }else{
                showAlert(this, "Error", "Faltan campos obligatorios por completar.")
            }
        }

        signUpButton.setOnClickListener {
            showSignUp(this)
        }
    }
}