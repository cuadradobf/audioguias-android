package com.example.audioguiasandroid.viewmodel

import android.content.ContentValues
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.audioguiasandroid.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

fun changePassword(activity: AppCompatActivity,actualPassword: String, newPassword: String){
    val credential = EmailAuthProvider.getCredential(Firebase.auth.currentUser?.email.toString(), actualPassword)

    Firebase.auth.currentUser!!.reauthenticate(credential)
        .addOnCompleteListener {
            if (it.isSuccessful){
                Log.d(ContentValues.TAG, "User re-authenticated.")

                Firebase.auth.currentUser!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "User password updated.")
                            //showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.change_password_successfully))
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
    //showAlert(activity, activity.getString(R.string.information), "Se ha enviado a " + Firebase.auth.currentUser?.email.toString() + " un email de verificación.")
}

fun changeNameAndSurname(activity: AppCompatActivity, name: String, surname: String){
    val regex = Regex("^[A-Za-z ]+$")
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
                //showAlert(activity, "Informacion", "Se han guardado los cambios.")
            }
            .addOnFailureListener{ e ->
                Log.w(ContentValues.TAG, "Error getting user data.", e)
                showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.error_user_info))
            }
    }else{
        showAlert(activity,activity.getString(R.string.information),activity.getString(R.string.invalid_fields_name_surname))
    }
}

fun signUp(activity: AppCompatActivity, email: String, password: String, password2: String, name: String, surname: String, provider: String): Boolean{

    val regex = Regex("^[A-Za-z ]+$")
    val regexPassword = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$")

    if ((name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password2.isNotEmpty() && surname.isNotEmpty() && regex.matches(name) && regex.matches(surname)) || (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password2.isNotEmpty() && surname.isEmpty() && regex.matches(name))){
    }else{
        showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.invalid_fields_name_surname))
        return false
    }

    if (password == password2){
    }else{
        showAlert(activity, activity.getString(R.string.information), activity.getString(R.string.dont_match_passwords))
        return false
    }

    if (regexPassword.matches(password)){
    }else{
        showAlert(activity,activity.getString(R.string.information), activity.getString(R.string.password_requirements))
        return false
    }

    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                FirebaseFirestore.getInstance().collection("user")
                    .document(Firebase.auth.currentUser?.email.toString()).set(
                    hashMapOf(
                        "name" to name,
                        "surname" to surname,
                        "provider" to provider,
                        "rol" to "Standar"
                    )
                )
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "User data updated successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error updating user data.", e)
                    }
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

                //Manda correo de verificación
                userAuth!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "Email sent.")
                        }
                    }
            } else {
                showAlert(activity,activity.getString(R.string.information),activity.getString(R.string.error_create_account))
            }
        }
        return true
}
fun changeLanguage(activity: FragmentActivity, languages: Array<String>, checkedItem: Int, textView: TextView){
    val builder = AlertDialog.Builder(activity)
    val firebaseArray = arrayOf("ES", "EN")
    builder.setSingleChoiceItems(languages,checkedItem){dialog, item ->
        FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
            hashMapOf(
                "language" to firebaseArray[item]
            ),
            //Opcion para combinar los datos y que no los machaque
            SetOptions.merge()
        )
        textView.text = firebaseArray[item]
        dialog.dismiss()
        //TODO: implementar logica para cambiar de idioma
    }
    builder.create()

    builder.setPositiveButton("Cancelar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

fun changeUnitOfMeasurement(activity: FragmentActivity, units: Array<String>, checkedItem: Int, textView: TextView){
    val builder = AlertDialog.Builder(activity)
    builder.setSingleChoiceItems(units,checkedItem){dialog, item ->
        FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
            hashMapOf(
                "unitOfMeasurement" to units[item]
            ),
            //Opcion para combinar los datos y que no los machaque
            SetOptions.merge()
        )
        textView.text = units[item]
        dialog.dismiss()
        //TODO: implementar logica para cambiar la unidad de medida
    }
    builder.create()

    builder.setPositiveButton("Cancelar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

fun changeRedMode(activity: FragmentActivity, checkedItem: Int, textView: TextView){
    val builder = AlertDialog.Builder(activity)
    val modes = arrayOf(activity.getString(R.string.all_redMode), activity.getString(R.string.wifi_redMode),activity.getString(R.string.offline_redMode))
    val firebaseArray = arrayOf("all", "wifi", "offline")
    builder.setSingleChoiceItems(modes,checkedItem){dialog, item ->
        FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
            hashMapOf(
                "redMode" to firebaseArray[item]
            ),
            //Opcion para combinar los datos y que no los machaque
            SetOptions.merge()
        )
        textView.text = modes[item]
        dialog.dismiss()
        //TODO: implementar logica para cambiar el modo de red
    }
    builder.create()

    builder.setPositiveButton("Cancelar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

fun changeDownloadStorage(activity: FragmentActivity, modes: Array<String>, checkedItem: Int, textView: TextView){
    val builder = AlertDialog.Builder(activity)
    val firebaseArray = arrayOf("internal", "external")
    builder.setSingleChoiceItems(modes,checkedItem){dialog, item ->
        FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
            hashMapOf(
                "storage" to firebaseArray[item]
            ),
            //Opcion para combinar los datos y que no los machaque
            SetOptions.merge()
        )
        textView.text = modes[item]
        dialog.dismiss()
        //TODO: implementar logica para cambiar la ubicacion de descarga
    }
    builder.create()

    builder.setPositiveButton("Cancelar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

//TODO: implementar logica para main users
