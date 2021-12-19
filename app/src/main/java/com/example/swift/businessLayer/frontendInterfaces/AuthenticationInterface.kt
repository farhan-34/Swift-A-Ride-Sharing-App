package com.example.swift.businessLayer.frontendInterfaces

interface AuthenticationInterface {
    fun logIn(phoneNumber:String, password:String)
    fun otpVerification(phoneNumber:String)
    fun setNewPassword(phoneNumber:String, password:String)

//phone number, password (return data of user (rider -name -rating))
//forgetpassword () -otp verification
//setNewPaswword (phone number, password)

}