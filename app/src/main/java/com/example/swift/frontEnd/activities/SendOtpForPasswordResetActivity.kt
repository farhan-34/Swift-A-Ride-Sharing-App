package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_send_otp_for_password_reset.*

class SendOtpForPasswordResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_send_otp_for_password_reset)

        btn_send_OTP_passReset.setOnClickListener {
            startActivity(Intent(this, ReceiveOtpForPasswordResetActivity::class.java))
        }
    }

}