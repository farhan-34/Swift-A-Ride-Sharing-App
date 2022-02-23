package com.example.swift.frontEnd.rider.passwordReset

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_receive_otp_for_password_reset.*
import kotlinx.android.synthetic.main.activity_rider_sign_in_otp.*
import java.util.concurrent.TimeUnit

class ReceiveOtpForPasswordResetActivity : AppCompatActivity() {
    lateinit var otpToVerify:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_receive_otp_for_password_reset)

        //setting actionbar attributes
        val actionBar = supportActionBar
        actionBar!!.title = "OTP Verification"
        actionBar.setDisplayHomeAsUpEnabled(true)

        setupOTPInputs()
        val phoneNumber = intent.getStringExtra("mobile")
        receiveOTP_mobile_textView.text = phoneNumber


        FirebaseApp.initializeApp(this)
        var flag = 0
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber.toString())       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(
                        this@ReceiveOtpForPasswordResetActivity,
                        "Please check Internet Connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(
                    p0: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(p0, p1)
                    otpToVerify = p0
                }
            })          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)




        btn_receive_OTP_passReset.setOnClickListener {

            if(isOtpInputValid()){
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("phoneNumber", phoneNumber)
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                val otp =  otp_input_code1.text.toString() +
                        otp_input_code2.text.toString() +
                        otp_input_code3.text.toString() +
                        otp_input_code4.text.toString() +
                        otp_input_code5.text.toString() +
                        otp_input_code6.text.toString()

                var credential = PhoneAuthProvider.getCredential(otpToVerify, otp)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener {
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "otp Verification failed!", Toast.LENGTH_SHORT).show()
                    }
            }

        }

    }

    // check valid otp input and show error
    private fun isOtpInputValid(): Boolean {
        var flag = true
        if(otp_input_code1.text.isEmpty()){
            otp_input_code1.error = "Invalid OTP"
            flag = false
        }
        if(otp_input_code2.text.isEmpty()){
            otp_input_code2.error = "Invalid OTP"
            flag = false
        }
        if(otp_input_code3.text.isEmpty()){
            otp_input_code3.error = "Invalid OTP"
            flag = false
        }
        if(otp_input_code4.text.isEmpty()){
            otp_input_code4.error = "Invalid OTP"
            flag = false
        }
        if(otp_input_code5.text.isEmpty()){
            otp_input_code5.error = "Invalid OTP"
            flag = false
        }
        if(otp_input_code6.text.isEmpty()){
            otp_input_code6.error = "Invalid OTP"
            flag = false
        }
        return flag
    }

    // changing focus on next OTP input automatically
    private  fun setupOTPInputs(){

        otp_input_code1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    otp_input_code2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        otp_input_code2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    otp_input_code3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        otp_input_code3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    otp_input_code4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        otp_input_code4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    otp_input_code5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        otp_input_code5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    otp_input_code6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

    }
}