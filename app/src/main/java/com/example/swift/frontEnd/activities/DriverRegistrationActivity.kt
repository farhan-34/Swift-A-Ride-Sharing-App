package com.example.swift.frontEnd.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_driver_registration.*
import kotlinx.android.synthetic.main.activity_rider_register.*

class DriverRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_registration)
        supportActionBar?.hide()

        //setting drop down values of vehicle type in frontend
        val items = listOf("Bike", "Rickshaw", "Car")
        val adapter = ArrayAdapter(this, R.layout.gender_dropdown_list_item, items)
        registerDriver_VehicleTypeInput.setAdapter(adapter)

        //setting buttons
        DriverRegister_button.setOnClickListener {
            if (isInputsValid()) {
                startActivity(Intent(this, DriverMainActivity::class.java))
            }
        }
    }

    //check whether the inputs are valid or not,
    // and show errors
    private fun isInputsValid(): Boolean {
        var isValid = true


        // CNIC input validation
        if(registerDriver_CNICInput.text!!.isEmpty()) {
            registerDriver_CNICLayout.error = "Fill CNIC"
            isValid = false
        }else{
            registerDriver_CNICLayout.error = null
            registerDriver_CNICLayout.isErrorEnabled = false
        }

        // licence no input validation
        if(registerDriver_LicenseInput.text!!.isEmpty()) {
            registerDriver_LicenseLayout.error = "Fill Licence Number"
            isValid = false
        }else{
            registerDriver_LicenseLayout.error = null
            registerDriver_LicenseLayout.isErrorEnabled = false
        }

        // VehicleType input validation
        if(registerDriver_VehicleTypeInput.text!!.isEmpty()) {
            registerDriver_VehicleTypeLayout.error = "Select Vehicle Type"
            isValid = false
        }else{
            registerDriver_VehicleTypeLayout.error = null
            registerDriver_VehicleTypeLayout.isErrorEnabled = false
        }

        // VehicleCapacity input validation
        if(registerDriver_VehicleCapacityInput.text!!.isEmpty()) {
            registerDriver_VehicleCapacityLayout.error = "Put Vehicle Capacity"
            isValid = false
        }else{
            registerDriver_VehicleCapacityLayout.error = null
            registerDriver_VehicleCapacityLayout.isErrorEnabled = false
        }

        return isValid
    }

}