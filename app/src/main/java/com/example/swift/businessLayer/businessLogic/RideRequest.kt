package com.example.swift.businessLayer.businessLogic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RideRequest(
    var requestId: String? = "",
    var riderId: String? = "",
    var driverId: String? = "",
    var riderName:String = "",
    var riderRating: Double = 0.0,
    var sourceLocation:String = "",
    var destinationLocation:String = "",
    var vehicleType:String = ""
)
    : Parcelable
{
    fun createRequest() {}
}