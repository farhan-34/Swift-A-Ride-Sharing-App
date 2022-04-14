package com.example.swift.frontEnd.rider.panicButton

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import com.example.swift.R
import com.example.swift.businessLayer.session.RiderSession
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_panic_button.*
import kotlinx.android.synthetic.main.activity_rider_register.*
import net.rimoto.intlphoneinput.IntlPhoneInput


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
            if(isInputsValid()) {
            val phoneNumber = emergency_contact_input.text.toString()
            RiderSession.getCurrentUser { rider ->
                db.collection("EmergencyContact").document(rider.phoneNumber).get()
                    .addOnSuccessListener { document ->
                        // getting current phone numbers list
                        var phoneNumberList:String = document.get("contacts").toString()
                        // adding the new phone number to the list
                        if(phoneNumber !in phoneNumberList) {
                            phoneNumberList += ",$phoneNumber"

                        val contacts = mapOf(
                            "contacts" to phoneNumberList
                        )
                        // pushing the list to the database
                        db.collection("EmergencyContact").document(rider.phoneNumber).set(contacts)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Contact Added!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener{
                                Toast.makeText(
                                    requireContext(),
                                    "Contact not Added!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else{
                            Toast.makeText(
                                requireContext(),
                                "Contact already exist!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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
                        val message = "I am in danger. Please HELP ME!"
                        //sending message here to all the numbers
                        for (number in phoneNumberList){
                            val sentPI: PendingIntent = PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), 0)
                            SmsManager.getDefault().sendTextMessage(number, null, message, sentPI, null)
                        }
                    }
            }
        }
    }
    private fun isInputsValid(): Boolean {
        var isValid = true
        var flag = 1
        if(emergency_contact_input.text == null) {
            showPhoneError()
            Toast.makeText(requireContext(), "Enter Mobile", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        else if(emergency_contact_input.text.toString().trim().isEmpty()) {
            showPhoneError()
            Toast.makeText(requireContext(), "Enter Mobile", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        else {
            removePhoneError()
        }
        return isValid
    }

    private fun showPhoneError(){
        val border = GradientDrawable()
        border.setStroke(3, -0x4f0000) //red border with full opacity
        border.cornerRadius = 50F
        emergency_contact_input.setBackgroundDrawable(border)
    }
    private fun removePhoneError(){
        val border = GradientDrawable()
        border.setStroke(3, -0xFFA500) //reset color
        border.cornerRadius = 50F
        emergency_contact_input.setBackgroundDrawable(border)
    }
}