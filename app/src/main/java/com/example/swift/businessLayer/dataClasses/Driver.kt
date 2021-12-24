package com.example.swift.businessLayer.dataClasses

class Driver (
    override var riderId:String = "",
    override var name:String = "",
    override var email:String = "",
    override var age:String = "",
    override var gender:String = "",
    override var phoneNumber:String = "",
    override var rating: Double = 0.0,
    var cnic:String = "",
    var licenseNumber:String = "",
    var vehicleType:String = "",
    var vehicleCapacity:String = "",
    var driverId: String = ""
):Rider(riderId, name, email, age, gender, phoneNumber, rating){

}
