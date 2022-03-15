package com.example.swift.businessLayer.dataClasses

import android.location.Location

data class RideSession(
    var offerId:String = "",
    var driverId:String = "",
    var riderId: String = "",
    var rideState: String = "",
    var pickUpLocation: String = "",
    var dropOffLocation: String = ""
){}
