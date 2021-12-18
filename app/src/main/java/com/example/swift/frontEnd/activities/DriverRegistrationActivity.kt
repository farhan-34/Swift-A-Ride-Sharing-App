package com.example.swift.frontEnd.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_driver_registration.*
import kotlinx.android.synthetic.main.activity_rider_register.*

class DriverRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_registration)
        supportActionBar?.hide()

        val items = listOf("Bike", "Rickshaw", "Car")
        val adapter = ArrayAdapter(this, R.layout.gender_dropdown_list_item, items)
        registerDriver_VehicleTypeInput.setAdapter(adapter)

        DriverRegister_button.setOnClickListener{
            startActivity(Intent(this, DriverMainActivity::class.java))
        }
    }
}