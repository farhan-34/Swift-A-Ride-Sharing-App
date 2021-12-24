package com.example.swift.frontEnd.activities

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.AdListener




class SplashScreenActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

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


        //loadAd()


        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,getString(R.string.Swift_Frontpage_ad_Testid), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(ContentValues.TAG, adError?.message)
                mInterstitialAd = null
                var homeIntent = Intent(this@SplashScreenActivity,SignInActivity::class.java)
                val splashScreenTimeOut = 1000

                Handler(Looper.getMainLooper()).postDelayed({

                    startActivity(homeIntent)
                    finish()
                }, splashScreenTimeOut.toLong())

            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(ContentValues.TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        var homeIntent = Intent(this@SplashScreenActivity,SignInActivity::class.java)
                        val splashScreenTimeOut = 1000

                        Handler(Looper.getMainLooper()).postDelayed({

                            startActivity(homeIntent)
                            finish()
                        }, splashScreenTimeOut.toLong())
                    }

                    override fun onAdDismissedFullScreenContent() {

                        super.onAdDismissedFullScreenContent()
                        Log.d(ContentValues.TAG, "Ad was Started.")
                        var homeIntent = Intent(this@SplashScreenActivity,SignInActivity::class.java)
                        val splashScreenTimeOut = 10

                        Handler(Looper.getMainLooper()).postDelayed({

                            startActivity(homeIntent)
                            finish()
                        }, splashScreenTimeOut.toLong())


                    }


                }
                mInterstitialAd?.show(this@SplashScreenActivity)


            }


        })



    }


}