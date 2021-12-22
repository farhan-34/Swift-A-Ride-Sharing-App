package com.example.swift.businessLayer.dataClasses

import com.google.android.gms.ads.interstitial.InterstitialAd
import java.io.Serializable

data class AdDataVarable(
    var mInterstitialAd: InterstitialAd? = null
): Serializable