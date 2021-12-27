package com.example.swift.businessLayer.Common

import android.widget.TextView
import com.example.swift.businessLayer.dataClasses.DriverGeo
import com.google.android.gms.maps.model.Marker
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Common {

    val offersReceived: MutableSet<String> = HashSet<String>()

    fun setWelcomeTextMessage(welcomeTxt: TextView) {
        val hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        if (hours in 1..12){
            welcomeTxt.text = StringBuilder("Good morning")
        }
        else if (hours in 13..17){
            welcomeTxt.text = StringBuilder("Good afternoon")
        }
        else {
            welcomeTxt.text = StringBuilder("Good evening")
        }
    }

    const val DRIVER_LOCATION_REF: String = "DriversLocation"
    val markerList: MutableMap<String, Marker> = HashMap<String, Marker>()
    val driversFound: MutableMap<String, DriverGeo> = HashMap<String,DriverGeo>()
    fun decodePoly(encoded: String): List<com.google.android.gms.maps.model.LatLng> {
        val poly = ArrayList<com.google.android.gms.maps.model.LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = com.google.android.gms.maps.model.LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }



}