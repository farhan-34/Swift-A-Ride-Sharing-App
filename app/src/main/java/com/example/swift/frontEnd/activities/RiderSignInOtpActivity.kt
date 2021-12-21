package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_rider_sign_in_otp.*

class RiderSignInOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_rider_sign_in_otp)

        //setting actionbar attributes
        val actionBar = supportActionBar
        actionBar!!.title = "OTP Verification"
        actionBar.setDisplayHomeAsUpEnabled(true)

        setupOTPInputs()

        rider_signIn_OTP_mobile_textView.text = intent.getStringExtra("mobile")

        btn_rider_signIn_OTP_verify.setOnClickListener {

            if(isOtpInputValid()){
                val intent = Intent(this, RiderMainActivity::class.java)

                startActivity(intent)
                finish()
            }

        }

    }

    // check valid otp input and show error
    private fun isOtpInputValid(): Boolean {
        var flag = true
        if(rider_signIn_otp_input_code1.text.isEmpty()){
            rider_signIn_otp_input_code1.error = "Invalid OTP"
            flag = false
        }
        if(rider_signIn_otp_input_code2.text.isEmpty()){
            rider_signIn_otp_input_code2.error = "Invalid OTP"
            flag = false
        }
        if(rider_signIn_otp_input_code3.text.isEmpty()){
            rider_signIn_otp_input_code3.error = "Invalid OTP"
            flag = false
        }
        if(rider_signIn_otp_input_code4.text.isEmpty()){
            rider_signIn_otp_input_code4.error = "Invalid OTP"
            flag = false
        }
        if(rider_signIn_otp_input_code5.text.isEmpty()){
            rider_signIn_otp_input_code5.error = "Invalid OTP"
            flag = false
        }
        if(rider_signIn_otp_input_code6.text.isEmpty()){
            rider_signIn_otp_input_code6.error = "Invalid OTP"
            flag = false
        }
        return flag
    }

    // changing focus on next OTP input automatically
    private  fun setupOTPInputs(){

        rider_signIn_otp_input_code1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_signIn_otp_input_code2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_signIn_otp_input_code2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_signIn_otp_input_code3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_signIn_otp_input_code3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_signIn_otp_input_code4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_signIn_otp_input_code4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_signIn_otp_input_code5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

        rider_signIn_otp_input_code5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()){
                    rider_signIn_otp_input_code6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no need to implement
            }

        })

    }
}