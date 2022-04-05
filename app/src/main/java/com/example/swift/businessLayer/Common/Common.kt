package com.example.swift.businessLayer.Common

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.TextView
import com.example.swift.businessLayer.dataClasses.DriverGeo
import com.example.swift.businessLayer.dataClasses.RideSession
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.io.IOException
import java.util.*


object Common {

    var rideSession: RideSession = RideSession()
    var endThread: Boolean = false
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

    fun getLocationFromAddress(context: Context, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return p1
    }

    const val DRIVER_LOCATION_REF: String = "DriversLocation"
    val markerList: MutableMap<String, Marker> = HashMap<String, Marker>()
    val driversFound: MutableMap<String, DriverGeo> = HashMap<String,DriverGeo>()
    fun decodePoly(encoded: String): ArrayList<LatLng> {
        val poly = ArrayList<LatLng>()
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
            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }



}