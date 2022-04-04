package com.example.swift.businessLayer.dataClasses

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class RideSession(
    var offerId:String = "",
    var driverId:String = "",
    var riderId: String = "",
    var rideState: String = "",
    var pickUpLocation: String = "",
    var dropOffLocation: String = "",
    var driverLocation: LatLng = LatLng(0.0,0.0)
){}
