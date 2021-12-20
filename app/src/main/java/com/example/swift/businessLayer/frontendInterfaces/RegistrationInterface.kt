package com.example.swift.businessLayer.frontendInterfaces

interface RegistrationInterface {
    fun createRider(riderId:String?, name:String?, gender:String?, age:String?, email:String?, phoneNumber:String?, password:String?):String
    fun otpVerification(phoneNumber:String)
//register the user (Boolean)
//otp working
}