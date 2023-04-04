package com.example.audioguiasandroid.view

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.audioguiasandroid.HomeActivity
import com.example.audioguiasandroid.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        setup()
    }

    private fun setup(){
        title = "Verificación de cuenta"

        val verifyAccountButton = findViewById<Button>(R.id.verifyAccount_Verify)
        val backButton = findViewById<Button>(R.id.backButton_Verify)

        verifyAccountButton.setOnClickListener {
            //Manda correo de verificación
            val user = Firebase.auth.currentUser
            user!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "Email sent.")
                    }
                }
            showInfo("Se ha enviado a " + Firebase.auth.currentUser?.email.toString() + " un email de verificación.")
        }

        backButton.setOnClickListener {
            showHome()
        }
    }

    private fun showInfo(info: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Información")
        if (info.isNullOrEmpty()){
            builder.setMessage("Se ha producido un error al obtener la información.")
        }else{
            builder.setMessage(info)
        }
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}

