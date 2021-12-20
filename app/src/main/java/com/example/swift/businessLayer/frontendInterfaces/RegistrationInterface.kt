package com.example.swift.businessLayer.frontendInterfaces

interface RegistrationInterface {
    fun createRider(name:String, gender:String, age:Int, email:String, phoneNumber:String, password:String):String
    fun otpVerification(phoneNumber:String)
//register the user (Boolean)
//otp working
}