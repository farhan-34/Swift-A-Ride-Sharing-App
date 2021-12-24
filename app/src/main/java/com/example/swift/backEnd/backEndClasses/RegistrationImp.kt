package com.example.swift.backEnd.backEndClasses

import com.example.swift.backEnd.backEndInterfaces.Registration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationImp :Registration{//imp means implementation class
    override fun createRider(riderId:String?, name: String?, gender: String?, age: String?, email: String?, phoneNumber: String?, password: String?, isDriver:String?): String {
        val db = Firebase.firestore

    // Create a new user with a first and last name
        val rider = hashMapOf(
            "Age" to age,
            "Email" to email,
            "Gender" to gender,
            "Name" to name,
            "Password" to password,
            "RideId" to riderId,
            "isDriver" to isDriver
        )
        //check existing user first then go on to check otp
        // Add a new document with a generated ID
        db.collection("Rider").document(riderId!!).set(rider).addOnSuccessListener {  }
        return riderId
    }

    override fun otpVerification(phoneNumber: String) {
        TODO("Not yet implemented")
    }

}
