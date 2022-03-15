package com.example.swift.frontEnd.driver.rideSession

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.swift.R
import com.example.swift.databinding.ActivityDriverRideSessionBinding
import com.example.swift.frontEnd.Remote.IGoogleAPI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.rxjava3.disposables.CompositeDisposable

class DriverRideSessionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDriverRideSessionBinding


    //Routes
    private val compositeDisposable = CompositeDisposable()
    private lateinit var iGoogleAPI: IGoogleAPI
    private var blackPolyLine: Polyline?=null
    private var greyPolyline: Polyline?= null
    private var polylineOptions: PolylineOptions?=null
    private var blackPolyline0ptions: PolylineOptions?=null
    private var polylineList:ArrayList<LatLng>? = null
    private var originMarker: Marker?= null
    private var destinationMarker: Marker?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverRideSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if(!success)
                Log.e("ERROR", "Style parsing error")
        }catch (e: Resources.NotFoundException){
            Log.e("ERROR", e.message!!)
        }
    }
}