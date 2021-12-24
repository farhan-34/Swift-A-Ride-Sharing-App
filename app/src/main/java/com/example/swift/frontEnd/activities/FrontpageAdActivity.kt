package com.example.swift.frontEnd.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.AdDataVarable
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class FrontpageAdActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frontpage_ad)
        supportActionBar?.hide()

        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,getString(R.string.Swift_Frontpage_ad_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(ContentValues.TAG, adError?.message)
                mInterstitialAd = null
                val intent = Intent(this@FrontpageAdActivity,SignInActivity::class.java)
                startActivity(intent)

            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(ContentValues.TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        val intent = Intent(this@FrontpageAdActivity,SignInActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onAdDismissedFullScreenContent() {

                        super.onAdDismissedFullScreenContent()
                        Log.d(ContentValues.TAG, "Ad was Started.")
                        val intent = Intent(this@FrontpageAdActivity,SignInActivity::class.java)
                        startActivity(intent)


                    }


                }
                mInterstitialAd?.show(this@FrontpageAdActivity)


            }


        })






    }

    private fun showAd() {
        if(mInterstitialAd!=null){
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                }

                override fun onAdDismissedFullScreenContent() {

                    super.onAdDismissedFullScreenContent()
                    Log.d(ContentValues.TAG, "Ad was Started.")
                    val intent = Intent(this@FrontpageAdActivity,SignInActivity::class.java)
                    startActivity(intent)
                }

            }

            mInterstitialAd?.show(this)

        }else{

            Log.d(ContentValues.TAG, "Ad was not started.")
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)

        }

    }




}