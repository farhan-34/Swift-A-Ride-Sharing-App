package com.example.swift.businessLayer.dataClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Rider(
    open var riderId:String = "",
    open var name:String = "",
    open var email:String = "",
    open var age:String = "",
    open var gender:String = "",
    open var phoneNumber:String = "",
    open var isdriver:String = "j",
    open var isLastTimeDriverLogin:String = "",
    open var rating: Double = 0.0
    ) : Parcelable {

}