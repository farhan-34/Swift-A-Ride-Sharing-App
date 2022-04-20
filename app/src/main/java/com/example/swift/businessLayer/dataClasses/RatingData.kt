package com.example.swift.businessLayer.dataClasses

class RatingData(
    var ratingGiver:String,
    var ratingTaker:String,
    var messege:String,
    var rating: Double = 0.0
) {
    constructor(): this("","","")
}