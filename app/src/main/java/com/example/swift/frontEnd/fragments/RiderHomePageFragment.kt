package com.example.swift.frontEnd.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.dataClasses.Driver
import com.example.swift.businessLayer.dataClasses.DriverGeo
import com.example.swift.businessLayer.dataClasses.GeoQueryModel
import com.example.swift.frontEnd.Callback.FirebaseDriverInfoListener
import com.example.swift.frontEnd.Callback.FirebaseFailedListener
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class RiderHomePageFragment : Fragment(), OnMapReadyCallback, FirebaseDriverInfoListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    //location
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback


    //load driver
    var distance = 1.0
    var limitRange = 10.0
    var previousLocation:Location?=null
    var currentLocation:Location?=null

    var firstTime = true

    //listener for the driver
    lateinit var iFirebaseDriverInfoListener: FirebaseDriverInfoListener
    lateinit var iFirebaseFailure: FirebaseFailedListener

    var cityName = ""




    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rider_home_page, container, false)
    }

    private fun init() {

        iFirebaseDriverInfoListener = this

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 3000
        locationRequest.interval = 5000
        locationRequest.smallestDisplacement = 10f

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val newPos = LatLng(locationResult!!.lastLocation.latitude,locationResult.lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))

                //If the location of a user is changed then load all the drivers again
                if(firstTime){
                    currentLocation = locationResult.lastLocation
                    previousLocation = locationResult.lastLocation

                    firstTime = false
                }
                else{
                    previousLocation = currentLocation
                    currentLocation = locationResult.lastLocation
                }

                if(previousLocation?.distanceTo(currentLocation)!!/1000 <= limitRange){
                    loadAvailableDrivers()
                }
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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

        loadAvailableDrivers()
    }

    private fun loadAvailableDrivers() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnFailureListener{ e ->
                Snackbar.make(requireView(),e.message!!,Snackbar.LENGTH_SHORT).show()
            }.addOnSuccessListener { location ->
                //load all the drivers in the city
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                var addressList: List<Address> = ArrayList()
                try{
                    addressList = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    cityName = addressList[0].locality

                    //Query
                    val driverLocationRef = FirebaseDatabase.getInstance()
                        .getReference("DriversLocation")
                        .child(cityName)
                    val gf = GeoFire(driverLocationRef)
                    val geoQuery = gf.queryAtLocation(GeoLocation(location.latitude, location.longitude), distance)
                    geoQuery.removeAllListeners()

                    geoQuery.addGeoQueryEventListener(object :GeoQueryEventListener{
                        override fun onKeyEntered(key: String?, location: GeoLocation?) {
                            Common.driversFound.add(DriverGeo(key!!,location!!))
                        }

                        override fun onKeyExited(key: String?) {

                        }

                        override fun onKeyMoved(key: String?, location: GeoLocation?) {

                        }

                        override fun onGeoQueryReady() {
                            if(distance <= limitRange){
                                distance++
                                loadAvailableDrivers()
                            }else{
                                distance = 0.0
                                addDriverMarker()
                            }
                        }

                        override fun onGeoQueryError(error: DatabaseError?) {
                            Snackbar.make(requireView(), error!!.message, Snackbar.LENGTH_SHORT).show()
                        }

                    })

                    driverLocationRef.addChildEventListener(object : ChildEventListener{
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            var geoQueryModel = snapshot.getValue(GeoQueryModel::class.java)
                            val geoLocation = GeoLocation(geoQueryModel!!.l!![0],geoQueryModel!!.l!![1])
                            val driverGeo = DriverGeo(snapshot.key,geoLocation)
                            val newDriverLocation = Location("")
                            newDriverLocation.latitude = geoLocation.latitude
                            newDriverLocation.longitude = geoLocation.longitude
                            val newDistance = location.distanceTo(newDriverLocation)/1000  //in km
                            if(newDistance <= limitRange)
                                findDriverByKey(driverGeo)
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {

                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {

                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Snackbar.make(requireView(), error.message, Snackbar.LENGTH_SHORT).show()
                        }

                    })

                }
                catch(e:IOException){
                    Snackbar.make(requireView(), getString(R.string.permission_require), Snackbar.LENGTH_SHORT).show()
                }
            }

    }

    private fun addDriverMarker() {

        if(Common.driversFound.size > 0){
            io.reactivex.rxjava3.core.Observable.fromIterable(Common.driversFound)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {driverGeoModel:DriverGeo? ->
                        findDriverByKey(driverGeoModel)
                    },
                    {
                        t: Throwable? -> Snackbar.make(requireView(), t!!.message!!,Snackbar.LENGTH_SHORT).show()
                    }
                )
        }else{
            Snackbar.make(requireView(), "Driver not found",Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun findDriverByKey(driverGeoModel: DriverGeo?) {
        FirebaseFirestore.getInstance().collection("Driver")
            .whereEqualTo("driverId", driverGeoModel!!.key)
            .get().addOnSuccessListener{ docs ->
                if (docs.documents.size>0){
                    driverGeoModel.driverInfo = docs.documents[0].toObject(Driver::class.java)
                    iFirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeoModel)
                }

            }
            .addOnFailureListener{
                iFirebaseFailure.onFirebaseFailed(driverGeoModel.key!!.toString() + "not Found")
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMyLocationClickListener {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            userLatLng,
                            18f
                        )
                    )
                }
            //layout button for location (edit location of button)
            val locationButton = (mapFragment.requireView()
                .findViewById<View>("1".toInt())
                .parent!! as View).findViewById<View>("2".toInt())
            val params = locationButton.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_TOP, 0)
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            params.bottomMargin = 50
            true
        }


        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if(!success)
                Log.e("EDMT_ERROR", "Style parsing error")
        }catch (e:Resources.NotFoundException){
            Log.e("EDMT_ERROR", e.message!!)
        }

    }

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapFragment.getMapAsync(this)
        init()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDriverInfoLoadSuccess(driverGeo: DriverGeo) {
        //checking if marker is already set
        if(!Common.markerList.containsKey(driverGeo.key))
            Common.markerList.put(driverGeo.key!!,
                mMap.addMarker(MarkerOptions()
                    .position(LatLng(driverGeo!!.geoLocation!!.latitude, driverGeo.geoLocation!!.longitude))
                    .flat(true)
                    .title(driverGeo.driverInfo!!.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)))!!)

        if(!TextUtils.isEmpty(cityName)){
            val driverLocation = FirebaseDatabase.getInstance()
                .getReference(Common.DRIVER_LOCATION_REF)
                .child(cityName)
                .child(driverGeo!!.key!!)
            driverLocation.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.hasChildren()){
                        if(Common.markerList.get(driverGeo!!.key!!) != null){
                            val marker = Common.markerList.get(driverGeo!!.key!!)!!
                            marker.remove()
                            Common.markerList.remove(driverGeo!!.key!!)
                            driverLocation.removeEventListener(this)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(requireView(), error.message, Snackbar.LENGTH_SHORT).show()
                }

            })
        }


    }

}


