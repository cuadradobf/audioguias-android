package com.example.audioguiasandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class SignUpActivity : AppCompatActivity() {
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
                if (passwordEditText.toString() == password2EditText.toString()){
                    if (surnameEditText.text.isNotEmpty()){


                    }else{

                    }
                }else{
                    showAlert("Las contraseñas no coinciden.")
                }
            }else{
                showAlert("Faltan campos obligatorios por completar.")
            }
        }


    }

    private fun showAlert(exception: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        if (exception.isNullOrEmpty()){
            builder.setMessage("Se ha producido un error de registro.")
        }else{
            builder.setMessage(exception)
        }
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAuth(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }
}