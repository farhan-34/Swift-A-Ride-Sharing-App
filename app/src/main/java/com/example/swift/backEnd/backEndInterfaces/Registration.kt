package com.example.swift.backEnd.backEndInterfaces

interface Registration {
    fun createRider(name:String, gender:String, age:Int, email:String, phoneNumber:String, password:String)
    fun otpVerification(phoneNumber:String)
}