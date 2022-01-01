package com.example.swift.frontEnd.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.swift.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreenActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

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
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.

                gotoSignin()
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION)
        }



        }, splashScreenTimeOut.toLong())


    }

    fun gotoSignin(){
        var homeIntent = Intent(this@SplashScreenActivity, SignInActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                gotoSignin()
                // app.
            } else {
               finish()
            }
        }


    //permission
//    private val PERMISSIONS_FINE_LOCATION = 50
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        @NonNull permissions: Array<String>,
//        @NonNull grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        when ((requestCode)) {
//            PERMISSIONS_FINE_LOCATION -> {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//
//                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                    shouldShowRequestPermissionRationale()
//                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }


}