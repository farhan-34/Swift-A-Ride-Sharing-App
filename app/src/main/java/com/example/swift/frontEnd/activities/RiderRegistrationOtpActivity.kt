package com.example.swift.frontEnd.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import com.example.swift.businessLayer.businessLogic.RiderManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_receive_otp_for_password_reset.*
import kotlinx.android.synthetic.main.activity_rider_registration_otp.*

class RiderRegistrationOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_registration_otp)

        //setting actionbar attributes
        val actionBar = supportActionBar
        actionBar!!.title = "OTP Verification"
        actionBar.setDisplayHomeAsUpEnabled(true)

        setupOTPInputs()

        val db = Firebase.firestore
        riderRegisterOtp_phoneNo.text = intent.getStringExtra("phoneNumber").toString()
        val name = intent.getStringExtra("name").toString()
        val age = intent.getStringExtra("age").toString()
        val gender = intent.getStringExtra("gender").toString()
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()
        val phoneNumber = intent.getStringExtra("phoneNumber").toString()
        val verficationId = intent.getStringExtra("otpId").toString()
        var riderId = ""

        riderRegisterOtp_verify.setOnClickListener {
            //val intent = Intent(this, ResetPasswordActivity::class.java)

            //startActivity(intent)
            //finish()

            if(isOtpInputValid()) {
                val otp = rider_otp_input_code1.text.toString() +
                        rider_otp_input_code2.text.toString() +
                        rider_otp_input_code3.text.toString() +
                        rider_otp_input_code4.text.toString() +
                        rider_otp_input_code5.text.toString() +
                        rider_otp_input_code6.text.toString()


                var credential = PhoneAuthProvider.getCredential(verficationId, otp)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener {

                        //get id of the current user from the Authentication Firebase
                        riderId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        //make a hashmap o rider to store
                        val rider = hashMapOf(
                            "age" to age,
                            "email" to email,
                            "gender" to gender,
                            "name" to name,
                            "phoneNumber" to phoneNumber,
                            "password" to password,
                            "riderId" to riderId
                        )
                        //adding the rider in the backend
                        db.collection("Rider").document(phoneNumber!!).set(rider)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "User Registered Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, SignInActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "User not Registered!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, RiderRegisterActivity::class.java))
                                finish()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
                    }
            }


        }
    }


    // check valid otp input and show error
    private fun isOtpInputValid(): Boolean {
        var flag = true
        if(rider_otp_input_code1.text.isEmpty()){
            rider_otp_input_code1.error = "Invalid OTP"
            flag = false
        }
        if(rider_otp_input_code2.text.isEmpty()){
            rider_otp_input_code2.error = "Invalid OTP"
            flag = false
        }
        if(rider_otp_input_code3.text.isEmpty()){
            rider_otp_input_code3.error = "Invalid OTP"
            flag = false
        }
        if(rider_otp_input_code4.text.isEmpty()){
            rider_otp_input_code4.error = "Invalid OTP"
            flag = false
        }
        if(rider_otp_input_code5.text.isEmpty()){
            rider_otp_input_code5.error = "Invalid OTP"
            flag = false
        }
        if(rider_otp_input_code6.text.isEmpty()){
            rider_otp_input_code6.error = "Invalid OTP"
            flag = false
        }
        return flag
    }

    private  fun setupOTPInputs(){

        rider_otp_input_code1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_otp_input_code2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_otp_input_code2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_otp_input_code3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_otp_input_code3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_otp_input_code4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_otp_input_code4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_otp_input_code5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_otp_input_code5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_otp_input_code6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

    }
}