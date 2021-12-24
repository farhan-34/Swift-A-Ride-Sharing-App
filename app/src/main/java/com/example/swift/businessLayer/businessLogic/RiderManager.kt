package com.example.swift.businessLayer.businessLogic

import com.example.swift.backEnd.backEndClasses.RegistrationImp

class RiderManager (){
    fun createRider(riderId:String?, name:String?, gender:String?, age:String?, email:String?, phoneNumber:String?, password:String?, isDriver:String?) : String{
        val obj1 = RegistrationImp()
        return obj1.createRider(riderId, name, gender, age, email, phoneNumber, password, isDriver)
    }
    fun getRider(){
        //get rider object
    }
    fun updateRider(){
        //update the rider
    }
    fun switchToDriver(){

    }
    fun getRideHistory(){

    }
    fun getAllChats(){

    }

}