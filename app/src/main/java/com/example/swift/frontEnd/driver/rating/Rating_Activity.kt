package com.example.swift.frontEnd.driver.rating

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.RatingData
import com.example.swift.frontEnd.driver.main.DriverMainActivity
import com.example.swift.frontEnd.rider.riderMain.RiderMainActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_rating.*

class Rating_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        var driverId = intent.getStringExtra("DRIVER_ID")
        var riderId = intent.getStringExtra("RIDER_ID")
        var isDriver = intent.extras!!.getBoolean("IS_DRIVER")


        var submit_Btn = findViewById<Button>(R.id.submit_rating)
        submit_Btn.setOnClickListener {
            var ratingObj: RatingData = RatingData()

            if (isDriver){
                ratingObj.ratingGiver = driverId.toString()
                ratingObj.ratingTaker = riderId.toString()
                ratingObj.messege = rating_commentInput.text.toString()
                //val simpleRatingBar = findViewById<View>(R.id.rating_ratingBar) as RatingBar // initiate a rating bar
                ratingObj.rating = rating_ratingBar.rating.toDouble()



                val db = Firebase.firestore
                db.collection("RiderRating").document(riderId!!).collection("driverId").document().set(ratingObj)
                    .addOnSuccessListener {
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
            else{
                ratingObj.ratingGiver = riderId.toString()
                ratingObj.ratingTaker = driverId.toString()
                ratingObj.messege = rating_commentInput.text.toString()
                //val simpleRatingBar = findViewById<View>(R.id.rating_ratingBar) as RatingBar // initiate a rating bar
                ratingObj.rating = rating_ratingBar.rating.toDouble()



                val db = Firebase.firestore
                db.collection("DriverRating").document(driverId!!).collection("riderId").document().set(ratingObj)
                    .addOnSuccessListener {
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






    }
}