package com.example.swift.frontEnd.rider.splashScreen

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.swift.R
import com.example.swift.frontEnd.rider.signIn.SignInActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreenActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()


        val topAnim = AnimationUtils.loadAnimation(this, R.anim.car_logo_animation)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.swift_logo_animation)

        splash_car.startAnimation(topAnim)
        splash_swift.startAnimation(bottomAnim)


        val splashScreenTimeOut = 2000

        Handler(Looper.getMainLooper()).postDelayed({
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) {

                    gotoSignIn()
                    }
            } else {
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
                requestPermissionLauncher.launch(
                    Manifest.permission.SEND_SMS)
            }

        }, splashScreenTimeOut.toLong())

    }

    //intent to sign in
    private fun gotoSignIn(){
        val homeIntent = Intent(this@SplashScreenActivity, SignInActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow
                gotoSignIn()

            } else {
               finish()
            }
        }


}