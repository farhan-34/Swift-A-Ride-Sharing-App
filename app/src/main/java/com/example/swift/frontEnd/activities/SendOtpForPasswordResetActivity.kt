package com.example.swift.frontEnd.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.util.TimeUnit.values
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_send_otp_for_password_reset.*
import java.time.chrono.JapaneseEra.values

class SendOtpForPasswordResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_send_otp_for_password_reset)

        //setting actionbar attributes
        val actionBar = supportActionBar
        actionBar!!.title = "OTP Verification"
        actionBar.setDisplayHomeAsUpEnabled(true)

        //setting buttons
        btn_send_OTP_passReset.setOnClickListener {
            when {
                Otp_phoneNumber_passReset.text == null -> {
                    Toast.makeText(applicationContext, "Enter Mobile", Toast.LENGTH_SHORT).show()
                }
                Otp_phoneNumber_passReset.text.toString().trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Enter Mobile", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    var intent = Intent(this, ReceiveOtpForPasswordResetActivity::class.java)
                    intent.putExtra("mobile", Otp_phoneNumber_passReset.text.toString())
                    startActivity(intent)
                }
            }
        }
    }

}