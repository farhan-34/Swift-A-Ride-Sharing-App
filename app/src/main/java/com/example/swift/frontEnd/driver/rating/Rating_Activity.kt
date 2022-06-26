package com.example.swift.frontEnd.driver.rating

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.RatingData
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.driver.main.DriverMainActivity
import com.example.swift.frontEnd.rider.chat.RiderChatLogActivity.Companion.TAG
import com.example.swift.frontEnd.rider.riderMain.RiderMainActivity
import com.example.swift.frontEnd.rider.signIn.RiderSignInOtpActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlin.math.roundToInt

class Rating_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        var driverId = intent.getStringExtra("DRIVER_ID")
        var riderId = intent.getStringExtra("RIDER_ID")
        var isDriver = intent.extras!!.getBoolean("IS_DRIVER")

        rating_driverName.text = if (isDriver) "Taimur" else "Farhan"



        var submit_Btn = findViewById<Button>(R.id.submit_rating)
        submit_Btn.setOnClickListener {
            var ratingObj: RatingData = RatingData()

            if (isDriver){
                val db = Firebase.firestore
                val docRef = db.collection("RiderRating").document(riderId!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document == null) {
                            ratingObj.rating = rating_ratingBar.rating.toDouble()
                            ratingObj.count = 1

                            db.collection("RiderRating").document(riderId!!).set(ratingObj)
                                .addOnSuccessListener {

                                    db.collection("Rider").whereEqualTo("riderId", riderId!!).get()
                                        .addOnSuccessListener { doc ->
                                            if (doc.size() == 0) {
                                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                                            } else {

                                                val riderPhoneNo = doc.documents[0].data?.get("phoneNumber").toString()

                                                val riderToUpdate = mapOf(
                                                    "rating" to (ratingObj.rating * 100).roundToInt().toDouble() / 100
                                                )

                                                db.collection("Rider").document(riderPhoneNo).update(riderToUpdate)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this, " Main Rating is Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener{
                                                        Toast.makeText(this, " Information Not Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }

                                            }
                                        }
                                        .addOnFailureListener{
                                            it.printStackTrace()
                                        }


                                    Toast.makeText(
                                        this,
                                        "Rider rating given Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, DriverMainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Rider rating not Registered!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, DriverMainActivity::class.java))
                                    finish()
                                }


                        } else {

                            var preRating = document?.data?.get("rating").toString().toDouble()
                            var preCount = document?.data?.get("count").toString().toInt()

                            ratingObj.rating = rating_ratingBar.rating.toDouble()


                            var newRating:Double = ((preCount.toDouble()*preRating)+ratingObj.rating.toDouble())/(preCount+1).toDouble()
                            ratingObj.rating = newRating
                            ratingObj.count = preCount+1

                            db.collection("RiderRating").document(riderId!!).set(ratingObj)
                                .addOnSuccessListener {

                                    db.collection("Rider").whereEqualTo("riderId", riderId!!).get()
                                        .addOnSuccessListener { doc ->
                                            if (doc.size() == 0) {
                                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                                            } else {

                                                val riderPhoneNo = doc.documents[0].data?.get("phoneNumber").toString()

                                                val riderToUpdate = mapOf(
                                                    "rating" to (ratingObj.rating * 100).roundToInt().toDouble() / 100
                                                )

                                                db.collection("Rider").document(riderPhoneNo).update(riderToUpdate)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this, " Main Rating is Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener{
                                                        Toast.makeText(this, " Information Not Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }

                                            }
                                        }
                                        .addOnFailureListener{
                                            it.printStackTrace()
                                        }


                                    Toast.makeText(
                                        this,
                                        "Rider rating given Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, DriverMainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Rider rating not Registered!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, DriverMainActivity::class.java))
                                    finish()
                                }




                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }







            }
            else{


                val db = Firebase.firestore
                val docRef = db.collection("DriverRating").document(driverId!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document == null) {
                            ratingObj.rating = rating_ratingBar.rating.toDouble()
                            ratingObj.count = 1

                            db.collection("DriverRating").document(driverId!!).set(ratingObj)
                                .addOnSuccessListener {

                                    db.collection("Driver").whereEqualTo("driverId", driverId!!).get()
                                        .addOnSuccessListener { doc ->
                                            if (doc.size() == 0) {
                                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                                            } else {

                                                val riderPhoneNo = doc.documents[0].data?.get("phoneNumber").toString()

                                                val riderToUpdate = mapOf(
                                                    "rating" to (ratingObj.rating * 100).roundToInt().toDouble() / 100
                                                )

                                                db.collection("Driver").document(riderPhoneNo).update(riderToUpdate)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this, " Main Rating is Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener{
                                                        Toast.makeText(this, " Information Not Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }

                                            }
                                        }
                                        .addOnFailureListener{
                                            it.printStackTrace()
                                        }


                                    Toast.makeText(
                                        this,
                                        "Driver rating given Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, RiderMainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Driver rating not Registered!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, RiderMainActivity::class.java))
                                    finish()
                                }


                        } else {

                            var preRating = document?.data?.get("rating").toString().toDouble()
                            var preCount = document?.data?.get("count").toString().toInt()

                            ratingObj.rating = rating_ratingBar.rating.toDouble()


                            var newRating:Double = ((preCount.toDouble()*preRating)+ratingObj.rating.toDouble())/(preCount+1).toDouble()
                            ratingObj.rating = newRating
                            ratingObj.count = preCount+1

                            db.collection("DriverRating").document(driverId!!).set(ratingObj)
                                .addOnSuccessListener {

                                    db.collection("Driver").whereEqualTo("driverId", driverId!!).get()
                                        .addOnSuccessListener { doc ->
                                            if (doc.size() == 0) {
                                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                                            } else {

                                                val riderPhoneNo = doc.documents[0].data?.get("phoneNumber").toString()

                                                val riderToUpdate = mapOf(
                                                    "rating" to (ratingObj.rating * 100).roundToInt().toDouble() / 100
                                                )

                                                db.collection("Driver").document(riderPhoneNo).update(riderToUpdate)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this, " Main Rating is Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener{
                                                        Toast.makeText(this, " Information Not Updated!!!", Toast.LENGTH_SHORT).show()
                                                    }

                                            }
                                        }
                                        .addOnFailureListener{
                                            it.printStackTrace()
                                        }


                                    Toast.makeText(
                                        this,
                                        "Driver rating given Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, RiderMainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Driver rating not Registered!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, RiderMainActivity::class.java))
                                    finish()
                                }




                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }

            }


        }






    }
}