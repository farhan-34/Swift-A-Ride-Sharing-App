package com.example.swift.businessLayer.dataClasses

class RatingData(
    var count:Int,
    var rating: Double = 0.0
) {
    constructor(): this(0)
}