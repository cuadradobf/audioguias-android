package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.viewmodel.showAlert
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showMain
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class DeleteAccountActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleteaccount)

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
        title = getString(R.string.delete_account_title)
        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton_DeleteAccount)
        val backButton = findViewById<ImageView>(R.id.backButton_DeleteAccount)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText_DeleteAccount)

        deleteAccountButton.setOnClickListener {
            if (passwordEditText.text.toString().isBlank()){
                showAlert(this, getString(R.string.information), getString(R.string.incorrect_actual_password))
            }else{
                val credential = EmailAuthProvider.getCredential(Firebase.auth.currentUser?.email.toString(), passwordEditText.text.toString())

                Firebase.auth.currentUser!!.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Log.d(ContentValues.TAG, "User re-authenticated.")

                            Firebase.auth.currentUser!!.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(ContentValues.TAG, "User account deleted.")
                                    }else{
                                        showAlert(this, getString(R.string.information), getString(R.string.error_delete_account))
                                    }
                                }
                            db.collection("user").document(Firebase.auth.currentUser?.email.toString()).delete()
                            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                            prefs.clear()
                            prefs.apply()

                            showAuth(this,getString(R.string.information), getString(R.string.success_delete_account))

                        }else{
                            showAlert(this, getString(R.string.information), getString(R.string.wrong_password))
                        }
                    }
            }

        }

        backButton.setOnClickListener {
            showMain(this, "home")
        }

    }
}