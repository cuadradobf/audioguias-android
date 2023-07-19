package com.example.audioguiasandroid.viewmodel

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

fun showAlert(activity: AppCompatActivity, title: String, exception: String){
    val builder = AlertDialog.Builder(activity)

    if (title.isNullOrEmpty()){
        builder.setTitle("Error")
    }else{
        builder.setTitle(title)
    }

    if (exception.isNullOrEmpty()){
        builder.setMessage("Se ha producido un error.")
    }else{
        builder.setMessage(exception)
    }

    builder.setPositiveButton("Aceptar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

