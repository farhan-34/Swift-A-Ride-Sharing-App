package com.example.swift.frontEnd.Callback

import com.example.swift.businessLayer.dataClasses.DriverGeo

interface FirebaseDriverInfoListener {
    fun onDriverInfoLoadSuccess(driverGeo: DriverGeo)
}