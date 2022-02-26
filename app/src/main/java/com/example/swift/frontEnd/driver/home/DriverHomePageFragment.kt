//package com.example.swift.frontEnd.driver.home
//
//
////Remove Later
//
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.Resources
//import android.location.Address
//import android.location.Geocoder
//import android.os.Bundle
//import android.os.Looper
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RelativeLayout
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import com.example.swift.R
//import com.firebase.geofire.GeoFire
//import com.firebase.geofire.GeoLocation
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MapStyleOptions
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import java.io.IOException
//import java.util.*
//
//class DriverHomePageFragment : Fragment(), OnMapReadyCallback {
//
//    companion object {
//        private lateinit var mMap: GoogleMap
//        private lateinit var mapFragment: SupportMapFragment
//
//        //locations
//        lateinit var locationRequest: com.google.android.gms.location.LocationRequest
//        lateinit var locationCallback: LocationCallback
//    }
//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//
//    //online system
//    private lateinit var onlineRef:DatabaseReference
//    private var currentUserRef:DatabaseReference?=null
//    private lateinit var driverLocationRef:DatabaseReference
//    private lateinit var geoFire: GeoFire
//
//
//    private val onlineValueEventListener = object :ValueEventListener{
//        override fun onDataChange(snapshot: DataSnapshot) {
//            if(snapshot.exists() && currentUserRef != null){
//                currentUserRef!!.onDisconnect().removeValue()
//            }
//
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            Snackbar.make(mapFragment.requireView(),error.message, Snackbar.LENGTH_LONG).show()
//        }
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_driver_home_page, container, false)
//    }
//
//    private fun init() {
//
//        onlineRef = FirebaseDatabase.getInstance().reference.child(".info/connected")
//
//        locationRequest = LocationRequest.create()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.fastestInterval = 15000 //15 sec
//        locationRequest.interval = 10000  //10 sec
//        locationRequest.smallestDisplacement = 50f  //50m
//
//        locationCallback = object: LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                val newPos = LatLng(locationResult!!.lastLocation.latitude,locationResult.lastLocation.longitude)
//                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))
//
//                //to store location (Longitude, Latitude) based on city names in real-time database
//                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
//                val addressList:List<Address>?
//                try{
//                    addressList = geoCoder.getFromLocation(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude,1)
//                    val cityName = addressList[0].locality
//
//                    driverLocationRef = FirebaseDatabase.getInstance().getReference("DriversLocation")
//                        .child(cityName)
//                    currentUserRef = driverLocationRef.child(
//                        FirebaseAuth.getInstance().currentUser!!.uid
//                    )
//
//                    geoFire = GeoFire(driverLocationRef)
//
//                    //update location
//                    geoFire.setLocation(
//                        FirebaseAuth.getInstance().currentUser!!.uid,
//                        GeoLocation(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
//                    ){ key:String?, error:DatabaseError? ->
//                        if(error != null){
//                            Log.println(Log.ERROR, "Error", error.message)
//                        }
//
//                    }
//                    registerOnlineSystem()
//
//                }catch(e: IOException){
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        mapFragment = childFragmentManager.findFragmentById(R.id.driver_map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//        init()
//    }
//
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        mMap.isMyLocationEnabled = true
//        mMap.uiSettings.isMyLocationButtonEnabled = true
//        mMap.setOnMyLocationClickListener {
//            fusedLocationProviderClient.lastLocation
//                .addOnSuccessListener { location ->
//                    val userLatLng = LatLng(location.latitude, location.longitude)
//                    mMap.animateCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                            userLatLng,
//                            18f
//                        )
//                    )
//                }
//            true
//        }
//        val locationButton = (mapFragment.requireView()
//            .findViewById<View>("1".toInt())
//            .parent!! as View).findViewById<View>("2".toInt())
//        val params = locationButton.layoutParams as RelativeLayout.LayoutParams
//        params.addRule(RelativeLayout.ALIGN_TOP, 0)
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
//        params.bottomMargin = 50
//
//        try {
//            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
//            if(!success)
//                Log.e("EDMT_ERROR", "Style parsing error")
//        }catch (e: Resources.NotFoundException){
//            Log.e("EDMT_ERROR", e.message!!)
//        }
//
//
//    }
//
//    override fun onDestroy() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//        geoFire.removeLocation(FirebaseAuth.getInstance().currentUser?.uid)
//        onlineRef.removeEventListener(onlineValueEventListener)
//        super.onDestroy()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapFragment.getMapAsync(this)
//        //init()
//    }
//
//    private fun registerOnlineSystem() {
//        onlineRef.addValueEventListener(onlineValueEventListener)
//    }
//}