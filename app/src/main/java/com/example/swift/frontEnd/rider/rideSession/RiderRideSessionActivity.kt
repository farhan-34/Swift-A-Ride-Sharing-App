package com.example.swift.frontEnd.rider.rideSession

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.core.app.ActivityCompat
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.EventBus.SelectedPlaceEvent
import com.example.swift.businessLayer.dataClasses.Ride
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.databinding.ActivityRiderRideSessionBinding
import com.example.swift.frontEnd.Remote.IGoogleAPI
import com.example.swift.frontEnd.Remote.RetroFitClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject

class RiderRideSessionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRiderRideSessionBinding

    //Session db
    val db = FirebaseDatabase.getInstance().getReference("RideSessions")

    //locations
    private lateinit var selectedPlaceEvent: SelectedPlaceEvent


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

        val pickUpLocation = intent.getStringExtra("pickUp").toString()
        val dropOffLocation = intent.getStringExtra("dropOff").toString()
        selectedPlaceEvent = SelectedPlaceEvent(Common.getLocationFromAddress(this,pickUpLocation)!!,
        Common.getLocationFromAddress(this, dropOffLocation)!!)

        binding = ActivityRiderRideSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initialize()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun initialize() {
        iGoogleAPI = RetroFitClient.instance!!.create(IGoogleAPI::class.java)
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
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if(!success)
                Log.e("ERROR", "Style parsing error")
        }catch (e: Resources.NotFoundException){
            Log.e("ERROR", e.message!!)
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceEvent.origin, 18f))

        mMap.setOnMyLocationClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceEvent.origin, 18f))

            true
        }

        drawPath()

    }

    private fun drawPath() {
        //request Api
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
            "less_driving",
            selectedPlaceEvent.originString, selectedPlaceEvent.destinationString,
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

                    polylineOptions = PolylineOptions()
                    polylineOptions!!.color(Color.GRAY)
                    polylineOptions!!.width(12f)
                    polylineOptions!!.startCap(SquareCap())
                    polylineOptions!!.jointType(JointType.ROUND)
                    polylineOptions!!.addAll(polylineList!!)
                    greyPolyline = mMap.addPolyline(polylineOptions!!)

                    blackPolylineOptions = PolylineOptions()
                    blackPolylineOptions!!.color(Color.BLACK)
                    blackPolylineOptions!!.width(5f)
                    blackPolylineOptions!!.startCap(SquareCap())
                    blackPolylineOptions!!.jointType(JointType.ROUND)
                    blackPolylineOptions!!.addAll(polylineList!!)
                    blackPolyLine = mMap.addPolyline(blackPolylineOptions!!)

                    //Animator
                    val valueAnimator = ValueAnimator.ofInt(0,100)
                    valueAnimator.duration = 1100
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

                    val latLngBound = LatLngBounds.Builder().include(selectedPlaceEvent.origin)
                        .include(selectedPlaceEvent.destination)
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

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBound,160))
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition!!.zoom-1))
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