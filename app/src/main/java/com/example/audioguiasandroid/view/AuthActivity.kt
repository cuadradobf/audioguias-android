package com.example.audioguiasandroid.view

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.audioguiasandroid.BaseActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.showResetPassword
import com.example.audioguiasandroid.viewmodel.showSignUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : BaseActivity() {

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
        if (email != null && Firebase.auth.currentUser != null){
            // El documento existe
            val authLayout = findViewById<ConstraintLayout>(R.id.authLayout_Auth)
            authLayout.visibility = View.INVISIBLE
            showMain(this, "home")
        }
    }

    private fun setup(){
        val emailEditText = findViewById<EditText>(R.id.emailEditText_Auth)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText_Auth)
        val createAccountTextView = findViewById<TextView>(R.id.createAccountTextView_Auth)
        val signInButton = findViewById<Button>(R.id.signInButton_Auth)
        val resetPasswordTextView = findViewById<TextView>(R.id.resetPasswordTextView_Auth)

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
                showAlert(this, getString(R.string.information), getString(R.string.required_fields))
            }
        }

        createAccountTextView.setOnClickListener {
            showSignUp(this)
        }
        resetPasswordTextView.setOnClickListener {
            showResetPassword(this)
        }
        //Al pulsar Enter sobre el edit text realiza la accion de pulsar el boton de acceso
        passwordEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                signInButton.performClick()
                true
            } else {
                false
            }
        }
    }
}