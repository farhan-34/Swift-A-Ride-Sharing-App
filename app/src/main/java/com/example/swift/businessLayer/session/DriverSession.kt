package com.example.swift.businessLayer.session

import com.example.swift.businessLayer.dataClasses.Rider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object DriverSession {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUser: DocumentReference
        get() = db.document("Rider/${FirebaseAuth.getInstance().currentUser?.phoneNumber ?: throw NullPointerException("UID is null.")}")

    fun getCurrentUser(onComplete: (Rider) -> Unit) {
        currentUser.get().addOnSuccessListener {
            onComplete(it.toObject(Rider::class.java)!!)
        }
    }
}