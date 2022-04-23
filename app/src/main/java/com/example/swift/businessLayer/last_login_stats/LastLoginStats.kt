package com.example.swift.businessLayer.last_login_stats

import android.se.omapi.Session
import com.example.swift.businessLayer.session.RiderSession

class LastLoginStats {
    fun setAsDriverLastLogin(){
        RiderSession.getCurrentUser { rider ->

        }
    }

    fun setAsRiderLastLogin(){

    }

    fun isDriverLastLogin(){

    }
}