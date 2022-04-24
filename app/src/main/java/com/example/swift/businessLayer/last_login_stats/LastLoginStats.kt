package com.example.swift.businessLayer.last_login_stats

import android.content.Context
import android.content.Intent
import android.se.omapi.Session
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.driver.main.DriverMainActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_rider_display_information.*

class LastLoginStats {

    fun setAsDriverLastLogin(){

        val db  = Firebase.firestore
        RiderSession.getCurrentUser { rider ->
            val riderToUpdate = mapOf(
                "isLastTimeDriverLogin" to "true",
            )
            db.collection("Rider").document(rider.phoneNumber).update(riderToUpdate)
        }

    }

    fun setAsRiderLastLogin(){

        val db  = Firebase.firestore
        RiderSession.getCurrentUser { rider ->
            val riderToUpdate = mapOf(
                "isLastTimeDriverLogin" to "false",
            )
            db.collection("Rider").document(rider.phoneNumber).update(riderToUpdate)
        }

    }


}