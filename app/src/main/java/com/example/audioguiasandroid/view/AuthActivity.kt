package com.example.audioguiasandroid.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.audioguiasandroid.HomeActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.showAlert
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
            showHome(email)
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
                        showHome(it.result?.user?.email ?: "")
                    }else{
                        showAlert(this, "Error", it.exception?.message.toString())
                    }
                }
            }else{
                showAlert(this, "Error", "Faltan campos obligatorios por completar.")
            }
        }

        signUpButton.setOnClickListener {
            showSignUp()
        }
    }

    private fun showHome(email: String/*, providerType: ProviderType*/){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email",email)
            //putExtra("provider", providerType.name)
        }
        startActivity(homeIntent)
    }

    private fun showSignUp(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}