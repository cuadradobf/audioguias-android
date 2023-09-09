package com.example.audioguiasandroid.viewmodel

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.audioguiasandroid.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

fun changePassword(activity: AppCompatActivity,actualPassword: String, newPassword: String){
    val credential = EmailAuthProvider.getCredential(Firebase.auth.currentUser?.email.toString(), actualPassword)
    val regexPassword = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$")
    if (!regexPassword.matches(newPassword)){
        showAlert(activity,activity.getString(R.string.information), activity.getString(R.string.password_requirements))
        return
    }
    Firebase.auth.currentUser!!.reauthenticate(credential)
        .addOnCompleteListener {
            if (it.isSuccessful){
                Log.d(ContentValues.TAG, "User re-authenticated.")

                Firebase.auth.currentUser!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "User password updated.")
                            Toast.makeText(activity, activity.getString(R.string.change_password_successfully), Toast.LENGTH_SHORT).show()
                            showUserProfile(activity)
                        }else{
                            showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.change_password_error))
                        }
                    }

            }else{
                showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.incorrect_actual_password))
            }
        }
}

fun sendEmailVerifyAccount(activity: AppCompatActivity){
    val user = Firebase.auth.currentUser
    user!!.sendEmailVerification()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "Email sent.")
            }
        }
    val text = activity.getString(R.string.verification_email_sent) + " " + Firebase.auth.currentUser?.email.toString() + "."
    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
}

fun changeNameAndSurname(activity: AppCompatActivity, name: String, surname: String){
    val regex = Regex("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+( [a-zA-ZÀ-ÿ\\u00f1\\u00d1]+)*$")
    if ((name.isNotEmpty() && regex.matches(name) && surname.isEmpty()) || (name.isNotEmpty() && surname.isNotEmpty() && regex.matches(name) && regex.matches(surname))){
        FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
            hashMapOf(
                "name" to name,
                "surname" to surname
            ),
            //Opcion para combinar los datos y que no los machaque
            SetOptions.merge()
        )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "User data updated successfully.")
                val userAuth = Firebase.auth.currentUser

                //Actualizar el nombre en auth
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                userAuth!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "User profile updated.")
                        }
                    }
                val text = activity.getString(R.string.changes_saved)
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{ e ->
                Log.w(ContentValues.TAG, "Error getting user data.", e)
                showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.error_user_info))
            }
    }else{
        showAlert(activity,activity.getString(R.string.information),activity.getString(R.string.invalid_fields_name_surname))
    }
}

fun signUp(activity: AppCompatActivity, email: String, password: String, password2: String, name: String, surname: String): Boolean{

    val regex = Regex("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+( [a-zA-ZÀ-ÿ\\u00f1\\u00d1]+)*$")
    val regexPassword = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$")

    if (name.isBlank() || email.isBlank() || password.isBlank() || password2.isBlank()){
        showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.invalid_fields_name_surname))
        return false
    }
    if (!regex.matches(name)){
        showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.invalid_fields_name_surname))
        return false
    }
    if (surname.isNotEmpty() && !regex.matches(surname)){
        showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.invalid_fields_name_surname))
        return false
    }

    if (password != password2){
        showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.dont_match_passwords))
        return false
    }

    if (!regexPassword.matches(password)){
        showAlert(activity,activity.getString(R.string.information), activity.getString(R.string.password_requirements))
        return false
    }


    return true
}

fun changeUnitOfMeasurement(activity: FragmentActivity, checkedItem: Int, textView: TextView){
    val builder = AlertDialog.Builder(activity)
    builder.setSingleChoiceItems(arrayOf("Km", "Mi"),checkedItem){dialog, item ->
        val prefs = activity.getSharedPreferences(activity.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        when(item){
            0 -> {
                prefs.putString("unitOfMeasurement", "Km")
                textView.text = "Km"
            }
            1 -> {
                prefs.putString("unitOfMeasurement", "Mi")
                textView.text = "Mi"
            }
        }
        prefs.apply()
        dialog.dismiss()
    }
    builder.create()

    builder.setPositiveButton("Cancelar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}
