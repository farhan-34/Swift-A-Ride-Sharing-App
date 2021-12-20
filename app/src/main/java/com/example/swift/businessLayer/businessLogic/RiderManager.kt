package com.example.swift.businessLayer.businessLogic

import com.example.swift.backEnd.backEndClasses.RegistrationImp

class RiderManager (){
    fun createRider(name:String, gender:String, age:Int, email:String, phoneNumber:String, password:String){

        val obj1 = RegistrationImp()
        obj1.createRider(name, gender, age, email, phoneNumber, password)



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