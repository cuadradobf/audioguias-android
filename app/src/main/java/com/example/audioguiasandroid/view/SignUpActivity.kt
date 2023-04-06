package com.example.audioguiasandroid.view

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.audioguiasandroid.HomeActivity
import com.example.audioguiasandroid.model.data.ProviderType
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.controller.showAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

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
            if (nameEditText.text.isNotEmpty() && emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() && password2EditText.text.isNotEmpty()){
                if (passwordEditText.text.toString() == password2EditText.text.toString()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            //TODO: Comprobar que textos validos (sin numeros ni simbolos)
                            db.collection("user").document(emailEditText.text.toString()).set(
                                hashMapOf("name" to nameEditText.text.toString(),
                                "surname" to surnameEditText.text.toString(),
                                "provider" to ProviderType.BASIC.name)
                            )

                            val user = Firebase.auth.currentUser

                            //Actualizar el nombre en auth
                            val profileUpdates = userProfileChangeRequest {
                                displayName = nameEditText.text.toString()
                            }
                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "User profile updated.")
                                    }
                                }

                            //Manda correo de verificación
                            user!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "Email sent.")
                                    }
                                }

                            showHome(it.result?.user?.email ?: "")
                        }else{
                            showAlert(this, "Error", it.exception?.message.toString())
                        }
                    }
                }else{
                    showAlert(this, "Error", "Las contraseñas no coinciden.")
                }
            }else{
                showAlert(this, "Error", "Faltan campos obligatorios por completar.")
            }
        }

        backButton.setOnClickListener {
            showAuth()
        }
    }

    private fun showAuth(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }

    private fun showHome(email: String){
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intent)
    }
}