package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()


        // TODO: uncomment and Set correct value later
        //val topAnim = AnimationUtils.loadAnimation(this,R.anim.car_logo_animation)
        //val bottomAnim = AnimationUtils.loadAnimation(this,R.anim.swift_logo_animation)

        //splash_car.startAnimation(topAnim)
        //splash_swift.startAnimation(bottomAnim)


        val splashScreenTimeOut = 1000
        val homeIntent = Intent(this, SignInActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code

            // TODO: Check user logged in or not if yes go to home screen otherwise go to login

            startActivity(homeIntent)
            finish()
        }, splashScreenTimeOut.toLong())

    }
}