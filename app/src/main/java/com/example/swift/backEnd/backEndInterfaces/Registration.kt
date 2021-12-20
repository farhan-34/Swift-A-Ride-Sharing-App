package com.example.swift.backEnd.backEndInterfaces

interface Registration {
    fun createRider(riderId:String?, name:String?, gender:String?, age:String?, email:String?, phoneNumber:String?, password:String?):String
    fun otpVerification(phoneNumber:String)
}