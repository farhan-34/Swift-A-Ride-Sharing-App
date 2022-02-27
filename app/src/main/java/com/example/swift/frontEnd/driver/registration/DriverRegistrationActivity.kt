package com.example.swift.frontEnd.driver.registration

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.driver.main.DriverMainActivity
import com.example.swift.frontEnd.rider.signIn.SignInActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_driver_registration.*

class DriverRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_registration)
        supportActionBar?.hide()

        //setting drop down values of vehicle type in frontend
        val items = listOf("Bike", "Rickshaw", "Car")
        val adapter = ArrayAdapter(this, R.layout.gender_dropdown_list_item, items)
        registerDriver_VehicleTypeInput.setAdapter(adapter)

        //getting firestore reference
        val db = FirebaseFirestore.getInstance()

        //setting buttons
        DriverRegister_button.setOnClickListener {
            if (isInputsValid()) {

                //getting values from frontennd
                val cnic = registerDriver_CNICInput.text.toString()
                val licenseNumber = registerDriver_LicenseInput.text.toString()
                val vehicleType = registerDriver_VehicleTypeInput.text.toString()
                val vehicleCapacity = registerDriver_VehicleCapacityInput.text.toString()
                RiderSession.getCurrentUser { rider ->

                    //making the driver to store
                    val driver = hashMapOf(
                        "name" to rider.name,
                        "age" to rider.age,
                        "email" to rider.email,
                        "gender" to rider.gender,
                        "cnic" to cnic,
                        "licenseNumber" to licenseNumber,
                        "vehicleType" to vehicleType,
                        "vehicleCapacity" to vehicleCapacity,
                        "driverId" to rider.riderId,
                        "phoneNumber" to rider.phoneNumber
                    )


                    //saving the driver in database
                    db.collection("Driver").document(rider.phoneNumber!!).set(driver)
                        .addOnSuccessListener {
                            val flag = "true"
                            db.collection("Rider").document(rider.phoneNumber).update(mapOf( "isdriver" to flag ))
                            Toast.makeText(this, "Driver Registered Successfully", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this, DriverMainActivity::class.java)
                            startActivity(intent)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Driver not Registered!!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, SignInActivity::class.java))
                            finish()
                        }
                }
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