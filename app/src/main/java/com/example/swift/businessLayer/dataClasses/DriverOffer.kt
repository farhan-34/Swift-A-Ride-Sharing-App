package com.example.swift.businessLayer.dataClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DriverOffer(
    var offerId:String = "",
    var driverId:String = "",
    var riderId: String = "",
    var driverName:String = "",
    var text:String = "",
    var profilePicture:String = "",
    var distance_between_driver_and_rider: Double = 0.0,
    var rating: Double = 0.0
): Parcelable {}
