package com.example.swift.backEnd.backEndClasses

import com.example.swift.backEnd.backEndInterfaces.Authentication
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationImp :Authentication{
    override fun logIn(phoneNumber: String, password: String) {
        TODO("Not yet implemented")
        var db = FirebaseFirestore.getInstance()
    }

    override fun otpVerification(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override fun setNewPassword(phoneNumber: String, password: String) {
        TODO("Not yet implemented")
    }

}