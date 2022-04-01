package com.example.swift.businessLayer.dataClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DriverOffer(
    var offerId:String = "",
    var driverId:String = "",
    var riderId: String = "",
    var name:String = "",
    var text:String = "",
    var profilePicture:String = "",
    var rating: Double = 0.0
): Parcelable {}
