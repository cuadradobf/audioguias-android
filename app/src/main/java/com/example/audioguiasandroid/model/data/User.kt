package com.example.audioguiasandroid.model.data

class User(email: String, name: String, provider: ProviderType) {
    var email: String = email
    var name: String = name
    var surname: String = ""
    var provider: ProviderType = provider
    var rol: String = "Standar"
    var profileImage: String = ""
    //var audioguiaList: List<Audioguia> = emptyList()

}