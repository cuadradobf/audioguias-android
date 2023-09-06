package com.example.audioguiasandroid.viewmodel

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.AboutUsActivity
import com.example.audioguiasandroid.view.AudioguideActitivity
import com.example.audioguiasandroid.view.AudioplayerActivity
import com.example.audioguiasandroid.view.AuthActivity
import com.example.audioguiasandroid.view.ChangePasswordActivity
import com.example.audioguiasandroid.view.ConfigurationActivity
import com.example.audioguiasandroid.view.ContactUsActivity
import com.example.audioguiasandroid.view.DeleteAccountActivity
import com.example.audioguiasandroid.view.HelpActivity
import com.example.audioguiasandroid.view.MainActivity
import com.example.audioguiasandroid.view.ResetPasswordActivity
import com.example.audioguiasandroid.view.SignUpActivity
import com.example.audioguiasandroid.view.UserProfileActivity
import com.example.audioguiasandroid.view.VerifyActivity
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.util.Locale

fun showAlert(activity: AppCompatActivity, title: String, exception: String){
    val builder = AlertDialog.Builder(activity)

    if (title.isEmpty()){
        builder.setTitle("Error")
    }else{
        builder.setTitle(title)
    }

    if (exception.isEmpty()){
        builder.setMessage(activity.getString(R.string.error_text))
    }else{
        builder.setMessage(exception)
    }

    builder.setPositiveButton(activity.getString(R.string.agree),null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

fun showMain(activity: AppCompatActivity, fragment: String){
    val intent = Intent(activity, MainActivity::class.java).apply {
        putExtra("fragment", fragment)
    }
    activity.startActivity(intent)
}
fun showUserProfile(activity: FragmentActivity){
    val intent = Intent(activity, UserProfileActivity::class.java)
    activity.startActivity(intent)
}

fun showAuth(activity: AppCompatActivity, title: String, exception: String){
    val intent = Intent(activity, AuthActivity::class.java).apply {
        putExtra("title", title)
        putExtra("exception", exception)
    }
    activity.startActivity(intent)
}
fun showAuth(activity: AppCompatActivity){
    val intent = Intent(activity, AuthActivity::class.java)
    activity.startActivity(intent)
}
fun showVerify(activity: AppCompatActivity){
    val intent = Intent(activity, VerifyActivity::class.java)
    activity.startActivity(intent)
}

fun showAudioplayer(activity: AppCompatActivity, audioGuideID: String){
    val intent = Intent(activity, AudioplayerActivity::class.java).apply {
        putExtra("audioGuide", audioGuideID)
    }
    activity.startActivity(intent)
}


fun showSignUp(activity: AppCompatActivity){
    val intent = Intent(activity, SignUpActivity::class.java)
    activity.startActivity(intent)
}
fun showResetPassword(activity: AppCompatActivity){
    val intent = Intent(activity, ResetPasswordActivity::class.java)
    activity.startActivity(intent)
}
fun showAudioguide(activity: FragmentActivity, audioGuideID: String) {
    val intent = Intent(activity, AudioguideActitivity::class.java).apply {
        putExtra("audioGuide", audioGuideID)
    }
    activity.startActivity(intent)
}

fun showChangePassword(activity: AppCompatActivity){
    val intent = Intent(activity, ChangePasswordActivity::class.java)
    activity.startActivity(intent)
}

fun showDeleteAccount(activity: AppCompatActivity){
    val intent = Intent(activity, DeleteAccountActivity::class.java)
    activity.startActivity(intent)
}

fun showConfiguration(activity: FragmentActivity){
    val intent = Intent(activity, ConfigurationActivity::class.java)
    activity.startActivity(intent)
}
fun showHelp(activity: FragmentActivity){
    val intent = Intent(activity, HelpActivity::class.java)
    activity.startActivity(intent)
}

fun showContactUs(activity: FragmentActivity){
    val intent = Intent(activity, ContactUsActivity::class.java)
    activity.startActivity(intent)
}

fun showAboutUs(activity: AppCompatActivity){
    val intent = Intent(activity, AboutUsActivity::class.java)
    activity.startActivity(intent)
}

fun changeLocationMode(
    activity: FragmentActivity,
    checkedItem: Int,
    imageView: ImageView?,
    modes: Array<String>,
    locationOn: Int,
    locationOff: Int,
    audioGuideAdapter: AudioGuideAdapter){

    val builder = AlertDialog.Builder(activity)
    builder.setSingleChoiceItems(modes,checkedItem){dialog, item ->

        var i = "off"
        when(item){
            0 -> {
                if (initRecyclerViewWithLocation(activity, 10.0, audioGuideAdapter)){
                    imageView?.setImageResource(locationOn)
                    i = "10"
                }
            }
            1 -> {
                if (initRecyclerViewWithLocation(activity, 50.0, audioGuideAdapter)){
                    imageView?.setImageResource(locationOn)
                    i = "50"
                }
            }
            2 -> {
                imageView?.setImageResource(locationOff)

                val language: String = Locale.getDefault().language

                if (language == "es"){
                    FirebaseFirestore.getInstance().collection("audioGuide")
                        .whereEqualTo("language", "es")
                        .get()
                        .addOnSuccessListener { result ->
                            val listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                            audioGuideAdapter.updateData(listAudioGuide)
                        }
                }else{
                    FirebaseFirestore.getInstance().collection("audioGuide")
                        .whereEqualTo("language", "en")
                        .get()
                        .addOnSuccessListener { result ->
                            val listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                            audioGuideAdapter.updateData(listAudioGuide)
                        }
                }

            }
        }
        FirebaseFirestore.getInstance().collection("user").document(Firebase.auth.currentUser?.email.toString()).set(
            hashMapOf(
                "locationMode" to i
            ),
            //Opcion para combinar los datos y que no los machaque
            SetOptions.merge()
        )
        dialog.dismiss()
    }

    builder.create()

    builder.setPositiveButton(activity.getString(R.string.cancel),null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

fun initRecyclerViewWithLocation(activity: FragmentActivity, radiusInKm : Double, audioGuideAdapter: AudioGuideAdapter): Boolean{
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
        return false
    }else{
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                val userLatitude = location?.latitude
                val userLongitude = location?.longitude
                if (userLatitude != null && userLongitude != null){

                    // Convertir el radio a grados
                    val radiusInDegrees = radiusInKm / 111.319

                    // Calcular los límites geográficos
                    val lowerLatitude = userLatitude - radiusInDegrees
                    val lowerLongitude = userLongitude - radiusInDegrees
                    val upperLatitude = userLatitude + radiusInDegrees
                    val upperLongitude = userLongitude + radiusInDegrees


                    // Crear los puntos geográficos para los límites
                    val lowerGeoPoint = GeoPoint(lowerLatitude, lowerLongitude)
                    val upperGeoPoint = GeoPoint(upperLatitude, upperLongitude)

                    val language = Locale.getDefault().language

                    if (language == "es"){
                        // Realizar la consulta
                        FirebaseFirestore.getInstance().collection("audioGuide")
                            .where(Filter.and(
                                Filter.equalTo("language", "es"),
                                Filter.greaterThan("location", lowerGeoPoint),
                                Filter.lessThan("location", upperGeoPoint)
                            ))
                            .get()
                            .addOnSuccessListener { result ->
                                val listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                                audioGuideAdapter.updateData(listAudioGuide)
                            }
                            .addOnFailureListener { e ->
                                // Manejo de errores en caso de que falle la consulta.
                                Log.w(ContentValues.TAG, "Error getting audio guide list with location.", e)
                            }
                    }else{
                        // Realizar la consulta
                        FirebaseFirestore.getInstance().collection("audioGuide")
                            .where(Filter.and(
                                Filter.equalTo("language", "en"),
                                Filter.greaterThan("location", lowerGeoPoint),
                                Filter.lessThan("location", upperGeoPoint)
                            ))
                            .get()
                            .addOnSuccessListener { result ->
                                val listAudioGuide = AudioGuideRepository().getAllAudioGuides(result)
                                audioGuideAdapter.updateData(listAudioGuide)
                            }
                            .addOnFailureListener { e ->
                                // Manejo de errores en caso de que falle la consulta.
                                Log.w(ContentValues.TAG, "Error getting audio guide list with location.", e)
                            }
                    }

                }
            }
    }
    return true
}
