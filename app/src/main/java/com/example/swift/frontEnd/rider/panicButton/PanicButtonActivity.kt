package com.example.swift.frontEnd.rider.panicButton

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.telephony.SmsManager
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import com.example.swift.businessLayer.session.RiderSession
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_panic_button.*


class PanicButtonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_panic_button)
        supportActionBar?.hide()
        val db = Firebase.firestore

        btn_add_panic_contact.setOnClickListener {
            val phoneNumber = emergency_contact_input.text.toString()
            RiderSession.getCurrentUser { rider ->
                db.collection("EmergencyContact").document(rider.phoneNumber!!).get()
                    .addOnSuccessListener { document ->
                        // getting current phone numbers list
                        var phoneNumberList:String = document.get("contacts").toString()
                        // adding the new phone number to the list
                        phoneNumberList += ",$phoneNumber"
                        val contacts = mapOf(
                            "contacts" to phoneNumberList
                        )
                        // pushing the list to the database
                        db.collection("EmergencyContact").document(rider.phoneNumber!!).set(contacts)
                    }
            }
        }
        panic_button.setOnClickListener {
            RiderSession.getCurrentUser { rider ->
                db.collection("EmergencyContact").document(rider.phoneNumber!!).get()
                    .addOnSuccessListener { document ->
                            //getting list of phone numbers from firestore
                            val phoneNumbers = document.get("contacts").toString()
                            //list of the phone numbers to send the message to
                            val phoneNumberList = phoneNumbers.split(",")

                            //TODO("Send message here")

                        }
            }
        }
    }

}