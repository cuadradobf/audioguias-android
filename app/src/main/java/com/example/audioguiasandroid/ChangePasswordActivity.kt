package com.example.audioguiasandroid

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthProvider
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
            showAuth("Los credenciales de tu cuenta se han perdido. Por favor, introducelos de nuevo.")
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

                    val credential = EmailAuthProvider.getCredential(Firebase.auth.currentUser?.email.toString(), actualPasswordEditText.text.toString())

                    Firebase.auth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(TAG, "User re-authenticated.")

                                Firebase.auth.currentUser!!.updatePassword(newPasswordEditText.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "User password updated.")
                                            showInfo("Contraseña cambiada correctamente.")
                                        }else{
                                            showAlert("Error al cambiar la contraseña.")
                                        }
                                    }

                            }else{
                                showAlert("Contraseña actual incorrecta. Error al re-autenticar.")
                            }
                        }

                }else{
                    showAlert("La nueva contraseña no coincide.")
                }
            }else{
                showAlert("Campos obligatorios por completar.")
            }
        }

        backButton.setOnClickListener {
            showProfile()
        }
    }

    private fun showAuth(exception: String){
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra("exception", exception)
        }
        startActivity(intent)
    }

    private fun showAlert(exception: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        if (exception.isNullOrEmpty()){
            builder.setMessage("Se ha producido un error.")
        }else{
            builder.setMessage(exception)
        }
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showInfo(info: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Información")
        if (info.isNullOrEmpty()){
            builder.setMessage("Se ha producido un error.")
        }else{
            builder.setMessage(info)
        }
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showProfile(){
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }

}