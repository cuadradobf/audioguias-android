package com.example.audioguiasandroid.model.data

class Audioguia(id: String, price: Double, description: String, location: Location) {
    var id: String = id
    var price: Double = price
        set(value) {
            if (value < 0 ){
                field = 0.0
            }else{
                field = value
            }
        }
    var description: String = description
    var location: Location = location
    var city: String = ""
    var country: String = ""
    var valoration: Double = 0.0
    var imageList: List<String> = emptyList()

    init {
        if (price < 0){
            this.price = 0.0
        }
    }
}