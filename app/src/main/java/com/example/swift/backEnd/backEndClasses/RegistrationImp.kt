package com.example.swift.backEnd.backEndClasses

import com.example.swift.backEnd.backEndInterfaces.Registration

class RegistrationImp :Registration{//imp means implementation class
    override fun createRider(
        name: String,
        gender: String,
        age: Int,
        email: String,
        phoneNumber: String,
        password: String
    ) {
        TODO("Not yet implemented")
    }

    override fun otpVerification(phoneNumber: String) {
        TODO("Not yet implemented")
    }

}
