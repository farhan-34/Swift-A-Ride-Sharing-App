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
import kotlinx.android.synthetic.main.activity_rider_registration_otp.*

class RiderRegistrationOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_registration_otp)
        setupOTPInputs()

        riderRegisterOtp_phoneNo.text = intent.getStringExtra("phoneNumber")
        val name = intent.getStringExtra("name")
        val age = intent.getStringExtra("age")?.toInt()
        val gender = intent.getStringExtra("gender")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val verficationId = intent.getStringExtra("otpId")

        riderRegisterOtp_verify.setOnClickListener {
            //val intent = Intent(this, ResetPasswordActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            //startActivity(intent)
            //finish()

            if (rider_otp_input_code1.text.isNotEmpty() &&
                rider_otp_input_code2.text.isNotEmpty() &&
                rider_otp_input_code3.text.isNotEmpty() &&
                rider_otp_input_code4.text.isNotEmpty() &&
                rider_otp_input_code5.text.isNotEmpty() &&
                rider_otp_input_code6.text.isNotEmpty())
                {
                    val otp = rider_otp_input_code1.text.toString() +
                            rider_otp_input_code2.text.toString() +
                            rider_otp_input_code3.text.toString() +
                            rider_otp_input_code4.text.toString() +
                            rider_otp_input_code5.text.toString() +
                            rider_otp_input_code6.text.toString()


                    var credential = PhoneAuthProvider.getCredential(verficationId!!, otp)
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnSuccessListener {
                                val obj = RiderManager()
                                obj.createRider(name, gender,age, email, phoneNumber,password)
                                startActivity(Intent(this, SignInActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener{
                                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
                            }
            }else{
                ///set error
            }



        }
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