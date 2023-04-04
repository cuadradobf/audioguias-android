package com.example.audioguiasandroid.model.data

class User(email: String, name: String, provider: ProviderType) {
    var email: String = email
    private var name: String = name
    private var surname: String = ""
    private var provider: ProviderType = provider
    private var rol: String = "Standar"
    private var profileImage: String = ""
    private var audioguiaList: List<Audioguia> = emptyList()

}