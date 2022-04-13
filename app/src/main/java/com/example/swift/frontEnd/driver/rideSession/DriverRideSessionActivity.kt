package com.example.swift.frontEnd.driver.rideSession

import android.Manifest
import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.EventBus.SelectedPlaceEvent
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.databinding.ActivityDriverRideSessionBinding
import com.example.swift.frontEnd.Remote.IGoogleAPI
import com.example.swift.frontEnd.Remote.RetroFitClient
import com.example.swift.frontEnd.driver.main.DriverMainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_driver_ride_session.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class DriverRideSessionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDriverRideSessionBinding


    //camera flag
    private var flag = true

    //locations
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //markers
    private var destMarker:Marker?= null
    private var picUpMarker: Marker?= null
    private var driverMarker: Marker?= null

    //Routes
    private val compositeDisposable = CompositeDisposable()
    private lateinit var iGoogleAPI: IGoogleAPI
    private var blackPolyLine: Polyline?=null
    private var greyPolyline: Polyline?= null
    private var polylineOptions: PolylineOptions?=null
    private var blackPolylineOptions: PolylineOptions?=null
    private var polylineList:ArrayList<LatLng>? = null
    private var originMarker: Marker?= null
    private var destinationMarker: Marker?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_driver_main)
        supportActionBar?.hide()

        binding = ActivityDriverRideSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session_cancel.visibility = View.GONE
        session_start.visibility = View.GONE
        session_finish.visibility = View.GONE



        session_start.setOnClickListener{
            var db = FirebaseDatabase.getInstance().getReference("RideSessions")
            db.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{
                        val session = it.getValue(RideSession::class.java)
                        if(session!!.driverId == FirebaseAuth.getInstance().currentUser!!.uid){
                            db.child(it.key!!).updateChildren(mapOf("rideState" to "In_Session"))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            session_cancel.visibility = View.GONE
            session_start.visibility = View.GONE
            session_cancel1.visibility = View.VISIBLE
        }

        session_cancel.setOnClickListener {
            cancelSession()
        }
        session_cancel1.setOnClickListener {
            cancelSession()
        }

        session_finish.setOnClickListener {
            val db = FirebaseDatabase.getInstance()
            val query = db.reference.child("RideSessions")
                .orderByChild("driverId")
                .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //TODO: Store the session info in history
                    for (docs in dataSnapshot.children) {
                        docs.ref.removeValue()
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                }
            })
            finish()
        }

        checkSession()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_driver_session) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkSession() {
        val db = FirebaseDatabase.getInstance().getReference("RideSessions")
        db.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val session = snapshot.getValue(RideSession::class.java)
                if(session!!.driverId == FirebaseAuth.getInstance().currentUser!!.uid){
                    finish()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun cancelSession() {
        val db = FirebaseDatabase.getInstance()
        val query1 = db.reference.child("RideSessions")
            .orderByChild("driverId")
            .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (docs in dataSnapshot.children) {
                    docs.ref.removeValue()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
//        try {
//            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
//            if(!success)
//                Log.e("ERROR", "Style parsing error")
//        }catch (e: Resources.NotFoundException){
//            Log.e("ERROR", e.message!!)
//        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceEvent.origin, 18f))
//
//        mMap.setOnMyLocationClickListener {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceEvent.origin, 18f))
//
//            true
//        }


        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 4000 //15 sec
        locationRequest.interval = 3000  //10 sec
        locationRequest.smallestDisplacement = 20f  //50m

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@DriverRideSessionActivity)

        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Toast.makeText(this@DriverRideSessionActivity,"location Updated", Toast.LENGTH_SHORT).show()
                val newPos = LatLng(locationResult!!.lastLocation.latitude,locationResult.lastLocation.longitude)
                var pickUp = ""

                var db = FirebaseDatabase.getInstance().getReference("RideSessions")
                db.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{
                            var state = ""
                            val session = it.getValue(RideSession::class.java)
                            if(session!!.driverId == FirebaseAuth.getInstance().currentUser!!.uid){
                                val result = FloatArray(1)
                                val dis = FloatArray(1)
                                Location.distanceBetween(newPos.latitude,newPos.longitude,
                                    session.pickUpLocation?.get("Lat") as Double,
                                    session.pickUpLocation?.get("Lng") as Double,result)

                                Location.distanceBetween(newPos.latitude,newPos.longitude,
                                    session.dropOffLocation?.get("Lat") as Double,
                                    session.dropOffLocation?.get("Lng") as Double,dis)
                                state = session.rideState
                                if(session.rideState == "Picking_Up" && result[0] < 400){
                                    db.child(it.key!!).updateChildren(mapOf("rideState" to "Waiting"))
                                    state = "Waiting"
                                }


                                if(session.rideState == "In_Session" && dis[0] < 400){
                                    db.child(it.key!!).updateChildren(mapOf("rideState" to "Reached"))
                                    state = "Reached"
                                }
                                db.child(it.key!!).updateChildren(mapOf("driverLat" to newPos.latitude))
                                db.child(it.key!!).updateChildren(mapOf("driverLng" to newPos.longitude))
                                val selectedPlace = SelectedPlaceEvent(origin = newPos,
                                    destination = LatLng(
                                        session.pickUpLocation?.get("Lat") as Double,
                                        session.pickUpLocation?.get("Lng") as Double
                                    )
                                    )
                                when (state) {
                                    "Waiting" -> {
                                        session_cancel.visibility = View.VISIBLE
                                        session_start.visibility = View.VISIBLE
                                        session_cancel1.visibility = View.GONE
                                    }
                                    "In_Session" -> {
                                        selectedPlace.destination = LatLng(session.dropOffLocation?.get("Lat") as Double,
                                            session.dropOffLocation?.get("Lng") as Double)
                                    }
                                }
                                if(state=="Reached"){
                                    selectedPlace.destination = LatLng(session.dropOffLocation?.get("Lat") as Double,
                                        session.dropOffLocation?.get("Lng") as Double)
                                    session_cancel1.visibility = View.GONE
                                    session_finish.visibility = View.VISIBLE
                                }
                                drawPath(selectedPlace, state)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

                if (ActivityCompat.checkSelfPermission(
                        this@DriverRideSessionActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@DriverRideSessionActivity,
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

//                db = FirebaseDatabase.getInstance().getReference("RideSessions")
//                db.addValueEventListener(object : ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })

            }

        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)

        //drawing path for the first time
//        drawPath(selectedPlaceEvent)


        //Checking location change in driver to update the path

    }

    private fun drawPath(selectedPlace: SelectedPlaceEvent, state:String) {
        iGoogleAPI = RetroFitClient.instance!!.create(IGoogleAPI::class.java)
        //request Api
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
            "less_driving",
            selectedPlace.originString, selectedPlace.destinationString,
            getString(R.string.google_maps_key))
        !!.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{returnResult->
                Log.d("Api_Return", returnResult)
                try{
                    val jsonObject = JSONObject(returnResult)
                    val jsonArray = jsonObject.getJSONArray("routes")
                    for(i in 0 until jsonArray.length()){
                        val route = jsonArray.getJSONObject(i)
                        val poly = route.getJSONObject("overview_polyline")
                        val polyline = poly.getString("points")
                        polylineList = Common.decodePoly(polyline)
                    }
                    blackPolyLine?.remove()
                    greyPolyline?.remove()
                    if(state != "Waiting" || state != "Reached"){
                        polylineOptions = PolylineOptions()
                        polylineOptions!!.color(Color.parseColor("#E87C35"))
                        polylineOptions!!.width(12f)
                        polylineOptions!!.startCap(SquareCap())
                        polylineOptions!!.jointType(JointType.ROUND)
                        polylineOptions!!.addAll(polylineList!!)
                        greyPolyline = mMap.addPolyline(polylineOptions!!)

                        blackPolylineOptions = PolylineOptions()
                        blackPolylineOptions!!.color(Color.parseColor("#fa8f5a"))
                        blackPolylineOptions!!.width(5f)
                        blackPolylineOptions!!.startCap(SquareCap())
                        blackPolylineOptions!!.jointType(JointType.ROUND)
                        blackPolylineOptions!!.addAll(polylineList!!)
                        blackPolyLine = mMap.addPolyline(blackPolylineOptions!!)

                        //Animator
                        val valueAnimator = ValueAnimator.ofInt(0,100)
                        valueAnimator.duration = 2100
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        valueAnimator.interpolator = LinearInterpolator()
                        valueAnimator.addUpdateListener { value->
                            val points = greyPolyline!!.points
                            val percentValue = value.animatedValue.toString().toInt()
                            val size = points.size
                            val newPoints = (size * (percentValue/100.0f)).toInt()
                            val p = points.subList(0, newPoints)
                            blackPolyLine!!.points = p
                        }
                        valueAnimator.start()
                    }
                    val latLngBound = LatLngBounds.Builder().include(LatLng(selectedPlace.origin.latitude,selectedPlace.origin.longitude))
                        .include(LatLng(selectedPlace.destination.latitude,selectedPlace.destination.longitude))
                        .build()

                    //add car for origin
                    val objects = jsonArray.getJSONObject(0)
                    val legs = objects.getJSONArray("legs")
                    val legsObjects = legs.getJSONObject(0)
                    val time = legsObjects.getJSONObject("duration")
                    val duration = time.getString("text")

                    val start_address = legsObjects.getString("start_address")
                    val end_address = legsObjects.getString("end_address")

                    addOriginMarker(duration, start_address)
                    addDestinationMarker(end_address)

                    if (state == "Picking_Up"){
                        picUpMarker = mMap.addMarker(MarkerOptions()
                            .position(selectedPlace.destination)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_pin))))
                    }

                    if (state == "In_Session"){
                        destMarker = mMap.addMarker(MarkerOptions()
                            .position(selectedPlace.destination)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_pin))))
                    }

                    driverMarker?.remove()
                    driverMarker = mMap.addMarker(MarkerOptions()
                        .position(selectedPlace.origin)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car)))

                    if(flag){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBound,160))
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition!!.zoom-1))
                        flag = false
                    }

                }catch (e:java.lang.Exception){
                    e.printStackTrace()
                }
            }
        )
    }

    private fun addDestinationMarker(endAddress: String) {
        //TODO: video number 21
    }

    private fun addOriginMarker(duration: String, startAddress: String) {

    }
}