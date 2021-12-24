package com.example.swift.businessLayer.Common

import com.example.swift.businessLayer.dataClasses.DriverGeo
import com.google.android.gms.maps.model.Marker

object Common {
    const val DRIVER_LOCATION_REF: String = "DriversLocation"
    val markerList: MutableMap<String, Marker> = HashMap<String, Marker>()
    val driversFound: MutableSet<DriverGeo> = HashSet<DriverGeo>()
}