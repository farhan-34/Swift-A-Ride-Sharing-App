package com.example.swift.backEnd.backEndClasses

import android.content.ContentValues.TAG
import android.util.Log
import com.example.swift.backEnd.backEndInterfaces.Registration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationImp :Registration{//imp means implementation class
    override fun createRider(name: String, gender: String, age: Int, email: String, phoneNumber: String, password: String):String {
        val db = Firebase.firestore

    // Create a new user with a first and last name
        val rider = hashMapOf(
            "Age" to 23,
            "Email" to "ali@gmail.com",
            "Gender" to "M",
            "Name" to "Ali"
        )
        var createdId:String = ""
        // Add a new document with a generated ID
        db.collection("Rider")
            .add(rider)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                createdId = documentReference.id
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        return createdId
    }

    override fun otpVerification(phoneNumber: String) {
        TODO("Not yet implemented")
    }

}
