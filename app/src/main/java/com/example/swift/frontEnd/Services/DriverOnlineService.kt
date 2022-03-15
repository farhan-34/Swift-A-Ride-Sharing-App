package com.example.swift.frontEnd.Services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.frontEnd.driver.rideSession.DriverRideSessionActivity
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.IOException
import java.util.*

class DriverOnlineService : Service() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    //locations
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //online system
    private lateinit var onlineRef: DatabaseReference
    private var currentUserRef: DatabaseReference?=null
    private lateinit var driverLocationRef: DatabaseReference
    private lateinit var geoFire: GeoFire

    private val onlineValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists() && currentUserRef != null){
                currentUserRef!!.onDisconnect().removeValue()
            }

        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        init()


        return super.onStartCommand(intent, flags, startId)
    }



    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        geoFire.removeLocation(FirebaseAuth.getInstance().currentUser?.uid)
        onlineRef.removeEventListener(onlineValueEventListener)
        super.onDestroy()
    }

    private fun init() {

        onlineRef = FirebaseDatabase.getInstance().reference.child(".info/connected")

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 15000 //15 sec
        locationRequest.interval = 10000  //10 sec
        locationRequest.smallestDisplacement = 50f  //50m

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val newPos = LatLng(locationResult!!.lastLocation.latitude,locationResult.lastLocation.longitude)
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))

                //to store location (Longitude, Latitude) based on city names in real-time database
                val geoCoder = Geocoder(this@DriverOnlineService, Locale.getDefault())
                val addressList:List<Address>?
                try{
                    addressList = geoCoder.getFromLocation(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude,1)
                    val cityName = addressList[0].locality

                    driverLocationRef = FirebaseDatabase.getInstance().getReference("DriversLocation")
                        .child(cityName)
                    currentUserRef = driverLocationRef.child(
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )

                    geoFire = GeoFire(driverLocationRef)

                    //update location
                    geoFire.setLocation(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        GeoLocation(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                    ){ key:String?, error:DatabaseError? ->
                        if(error != null){
                            Log.println(Log.ERROR, "Error", error.message)
                        }

                    }
                    registerOnlineSystem()

                }catch(e: IOException){
                    e.printStackTrace()
                }
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
    }

    private fun registerOnlineSystem() {
        onlineRef.addValueEventListener(onlineValueEventListener)
    }
}