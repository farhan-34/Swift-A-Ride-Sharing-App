package com.example.swift.frontEnd.rider.panicButton

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.swift.R
import com.example.swift.businessLayer.session.RiderSession
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_panic_button.*


class PanicButtonFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panic_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = Firebase.firestore

        btn_add_panic_contact.setOnClickListener {
            val phoneNumber = emergency_contact_input.text.toString()
            RiderSession.getCurrentUser { rider ->
                db.collection("EmergencyContact").document(rider.phoneNumber).get()
                    .addOnSuccessListener { document ->
                        // getting current phone numbers list
                        var phoneNumberList:String = document.get("contacts").toString()
                        // adding the new phone number to the list
                        phoneNumberList += ",$phoneNumber"
                        val contacts = mapOf(
                            "contacts" to phoneNumberList
                        )
                        // pushing the list to the database
                        db.collection("EmergencyContact").document(rider.phoneNumber).set(contacts)
                    }
            }
        }
        panic_button.setOnClickListener {
            RiderSession.getCurrentUser { rider ->
                db.collection("EmergencyContact").document(rider.phoneNumber).get()
                    .addOnSuccessListener { document ->
                        //getting list of phone numbers from firestore
                        val phoneNumbers = document.get("contacts").toString()
                        //list of the phone numbers to send the message to
                        val phoneNumberList = phoneNumbers.split(",")
                        //message to send
                        val message = "I am in panic"
                        //sending message here to all the numbers
                        for (number in phoneNumberList){
                            val sentPI: PendingIntent = PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), 0)
                            SmsManager.getDefault().sendTextMessage(number, null, message, sentPI, null)
                        }
                    }
            }
        }
    }
}