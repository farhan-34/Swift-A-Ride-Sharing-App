package com.example.swift.businessLayer.session

import com.example.swift.businessLayer.dataClasses.Rider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object RiderSession {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance()}

    private val currentUser: DocumentReference
        get() = db.document("Rider/${FirebaseAuth.getInstance().currentUser?.phoneNumber?: throw NullPointerException("Phone Number is null.")}")

    fun getCurrentUser(onComplete: (Rider) -> Unit) {
        currentUser.get().addOnSuccessListener {
            val obj = Rider()
            obj.riderId = it["riderId"] as String
            obj.phoneNumber= it["phoneNumber"] as String
            obj.isdriver= it["isdriver"] as String
            obj.name= it["name"] as String
            obj.email= it["email"] as String
            obj.gender= it["gender"] as String
            obj.isdriver= it["isdriver"] as String
            obj.age= it["age"] as String
            val rating = it["rating"] as Number
            obj.rating = rating.toDouble()
            obj.isLastTimeDriverLogin = it["isLastTimeDriverLogin"] as String
            onComplete(obj)
//                it.toObject(Rider::class.java)!!)
        }
    }
}