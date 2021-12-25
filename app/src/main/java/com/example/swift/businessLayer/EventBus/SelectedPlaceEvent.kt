package com.example.swift.businessLayer.EventBus

import com.google.android.gms.maps.model.LatLng

class SelectedPlaceEvent(var origin: LatLng,var destination: LatLng) {
    public val originString:String
    get() = java.lang.StringBuilder()
        .append(origin.latitude)
        .append(",")
        .append(origin.longitude)
        .toString()

    val destinationString:String
    get() = StringBuilder()
        .append(destination.latitude)
        .append(",")
        .append(destination.longitude)
        .toString()


}
