package com.example.swift.businessLayer.dataClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class RideSession(
    var offerId:String = "",
    var driverId:String = "",
    var riderId: String = "",
    var rideState: String = "",
    var pickUpLocation: MutableMap<String, @RawValue Any>? = null,
    var dropOffLocation: MutableMap<String,@RawValue Any>? = null,
    var driverLat: Double = 0.0,
    var driverLng: Double = 0.0,
    var vehicleType:String = "",
    var money: String = "",
    var midPoint:MutableMap<String, @RawValue Any>? = null,
    var midPointFlag: Boolean = false
):Parcelable{}
