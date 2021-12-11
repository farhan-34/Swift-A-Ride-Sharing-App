package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_receive_otp_for_password_reset.*
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_reset_password)

        resetPass_btn_reset.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}