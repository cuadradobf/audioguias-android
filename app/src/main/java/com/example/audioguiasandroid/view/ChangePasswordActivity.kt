package com.example.audioguiasandroid.view


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.changePassword
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showUserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

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
        title = "Cambio de contraseña"

        val actualPasswordEditText = findViewById<EditText>(R.id.actualPasswordEditText_ChagePassword)
        val newPasswordEditText = findViewById<EditText>(R.id.newPasswordEditText_ChagePassword)
        val newPasswordEditText2 = findViewById<EditText>(R.id.newPasswordEditText2_ChagePassword)
        val saveButton = findViewById<Button>(R.id.saveButton_ChangePassword)
        val backButton = findViewById<Button>(R.id.backButton_ChangePassword)


        saveButton.setOnClickListener {
            if (actualPasswordEditText.text.isNotEmpty() && newPasswordEditText.text.isNotEmpty() && newPasswordEditText2.text.isNotEmpty()){
                if (newPasswordEditText.text.toString() == newPasswordEditText2.text.toString()){

                    changePassword(this, actualPasswordEditText.text.toString(), newPasswordEditText.text.toString())

                }else{
                    showAlert(this, getString(R.string.information), getString(R.string.dont_match_passwords))
                }
            }else{
                showAlert(this, getString(R.string.information), getString(R.string.required_fields))
            }
        }

        backButton.setOnClickListener {
            showUserProfile(this)
        }

        //Al pulsar Enter sobre el edit text realiza la accion de pulsar el boton de guardado
        newPasswordEditText2.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                saveButton.performClick()
                true
            } else {
                false
            }
        }
    }


}