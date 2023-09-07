package com.example.audioguiasandroid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.signUp

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setup()
    }

    private fun setup(){
        title = getString(R.string.sign_up_title)

        val nameEditText = findViewById<EditText>(R.id.nameEditText_SignUp)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText_SignUp)
        val emailEditText = findViewById<EditText>(R.id.emailEditText_SignUp)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText_SignUp)
        val password2EditText = findViewById<EditText>(R.id.password2EditText_SignUp)
        val signUpButton = findViewById<Button>(R.id.signUpButtton_SignUp)
        val backButton = findViewById<ImageView>(R.id.backButton_SignUp)


        signUpButton.setOnClickListener {
            if (signUp(this, emailEditText.text.toString(), passwordEditText.text.toString(), password2EditText.text.toString(), nameEditText.text.toString(), surnameEditText.text.toString())){
                showMain(this, "home")
            }
        }

        backButton.setOnClickListener {
            showAuth(this)
        }

        //Al pulsar Enter sobre el edit text realiza la accion de pulsar el boton de registro
        password2EditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                signUpButton.performClick()
                true
            } else {
                false
            }
        }
    }
}