package com.example.swift.backEnd.backEndInterfaces

interface Authentication {
    fun logIn(phoneNumber:String, password:String)
    fun otpVerification(phoneNumber:String)
    fun setNewPassword(phoneNumber:String, password:String)
}