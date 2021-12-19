package com.example.swift.businessLayer.businessLogic

class RideRequest(
    var requestId: String?,
    var riderId: String?,
    var riderName:String,
    var riderRating: String,
    var sourceLocation:String,
    var destinationLocation:String,
    var vehicleType: String?
){
    fun createRequest() {}
}