package com.example.audioguiasandroid.view

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.audioguiasandroid.R

import com.example.audioguiasandroid.viewmodel.changeNameAndSurname
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showChangePassword
import com.example.audioguiasandroid.viewmodel.showDeleteAccount
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.showVerify

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.IOException

class UserProfileActivity : AppCompatActivity() {
    private var db = FirebaseFirestore.getInstance()
    private var imageUri : Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,getString(R.string.information),getString(R.string.lost_credentials))
        }else{
            if (Firebase.auth.currentUser?.isEmailVerified == true){
                setup()
            }else{
                showVerify(this)
            }
        }
    }

    private fun setup(){
        title = getString(R.string.profile_title)

        val userImage = findViewById<ImageView>(R.id.userImageView_UserProfile)
        val removeImage = findViewById<ImageView>(R.id.removeImageView_UserProfile)
        val emailTextView = findViewById<TextView>(R.id.emailTextView_UserProfile)
        val nameEditText = findViewById<EditText>(R.id.nameEditText_UserProfile)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText_UserProfile)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton_UserProfile)
        val saveButton = findViewById<Button>(R.id.saveButton_UserProfile)
        val logOutButton = findViewById<Button>(R.id.logOutButton_UserProfile)
        val backButton = findViewById<Button>(R.id.backButton_UserProfile)

        val storage = Firebase.storage
        val storageRef = storage.reference
        val userRef: StorageReference = storageRef.child("images").child(Firebase.auth.currentUser?.email.toString() + "/profile")

        //Descargar imagen de perfil
        userRef.downloadUrl.addOnSuccessListener {
            Picasso.get()
                .load(it)
                .into(userImage)
        }.addOnFailureListener {
            storageRef.child("images/default/profile.png").downloadUrl.addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .into(userImage)
            }
        }

        //opcion para quitar la imagen de perfil
        removeImage.setOnClickListener{
            userRef.delete()
            storageRef.child("images/default/profile.png").downloadUrl.addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .into(userImage)
            }
        }

        emailTextView.text = Firebase.auth.currentUser?.email.toString()
        db.collection("user").document(Firebase.auth.currentUser?.email.toString()).get().addOnSuccessListener {
            nameEditText.setText(it.get("name") as String?)
            surnameEditText.setText(it.get("surname") as String?)
        }

        userImage.setOnClickListener {
            selectImageFromGallery(it)
        }

        changePasswordButton.setOnClickListener {
            showChangePassword(this)
        }

        saveButton.setOnClickListener {
            changeNameAndSurname(this, nameEditText.text.toString(), surnameEditText.text.toString())
        }

        logOutButton.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,getString(R.string.information), getString(R.string.log_out_info))
        }

        backButton.setOnClickListener {
            showMain(this, "home")
        }
        //Al pulsar Enter sobre el edit text realiza la accion de pulsar el boton de guardado
        surnameEditText.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                saveButton.performClick()
                true
            } else {
                false
            }
        }

    }

    private fun uploadImage(){
        if (imageUri != null){
            val storage = Firebase.storage
            var storageRef = storage.reference
            var userRef: StorageReference = storageRef.child("images").child(Firebase.auth.currentUser?.email.toString() + "/profile")

            try {
                val inputStream = contentResolver.openInputStream(imageUri!!)
                inputStream?.let {
                    val uploadTask = userRef.putStream(it)
                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        // La imagen se subió exitosamente
                        Log.d(ContentValues.TAG, "User image uploaded successfully.")
                        //URL de descarga
                        //val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
                        val intent = Intent(this, UserProfileActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener { exception ->
                        // Ocurrió un error al subir la imagen
                        Log.d(ContentValues.TAG, exception.message.toString())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }
    fun selectImageFromGallery(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data

            // Subir la imagen a Firebase Storage
            uploadImage()
        }
    }
}

