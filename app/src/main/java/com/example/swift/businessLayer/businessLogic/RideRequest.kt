package com.example.swift.businessLayer.businessLogic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class RideRequest(
    var requestId: String? = "",
    var riderId: String? = "",
    var driverId: String? = "",
    var riderName:String = "",
    var riderRating: Double = 0.0,
    var sourceLocation:MutableMap<String,@RawValue Any>? = null,
    var destinationLocation:MutableMap<String, @RawValue Any>? = null,
    var vehicleType:String = "",
    var midPoint:MutableMap<String,@RawValue Any>? = null,
    var midPointFlag:Boolean = false,
    var distance_between_driver_and_rider: Double = 0.0
)
    : Parcelable
{}