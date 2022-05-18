package com.example.swift.frontEnd.rider.rideSession

import android.Manifest
import android.animation.ValueAnimator
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.EventBus.SelectedPlaceEvent
import com.example.swift.businessLayer.dataClasses.Driver
import com.example.swift.businessLayer.dataClasses.Ride
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.databinding.ActivityRiderRideSessionBinding
import com.example.swift.frontEnd.Remote.IGoogleAPI
import com.example.swift.frontEnd.Remote.RetroFitClient
import com.example.swift.frontEnd.rider.riderMain.RiderMainActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rider_ride_session.*
import org.json.JSONObject

class RiderRideSessionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRiderRideSessionBinding

    //camera counter
    private var flag = true

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

    //mid routes
    private var blackPolyLineMid: Polyline?=null
    private var greyPolylineMid: Polyline?= null
    private var polylineOptionsMid: PolylineOptions?=null
    private var blackPolylineOptionsMid: PolylineOptions?=null
    private var polylineListMid:ArrayList<LatLng>? = null

    //markers
    private var marker:Marker?= null
    private var destMarker:Marker?= null
    private var pickUpMarker:Marker?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_driver_main)
        supportActionBar?.hide()

        binding = ActivityRiderRideSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_rider_session) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initialize()

        riderSession_panic.setOnClickListener {
            //fetch information about driver
            val db = FirebaseDatabase.getInstance().getReference("RideSessions")
            RiderSession.getCurrentUser { rider ->
                db.get().addOnSuccessListener {
                    it.children.forEach { child ->
                        val session = child.getValue(RideSession::class.java)
                        if (session?.riderId == rider.riderId){
                            val lat = session.driverLat
                            val long = session.driverLng
                            val driverId = session.driverId

                            //send message for help
                            val db1 = Firebase.firestore
                            db1.collection("EmergencyContact").document(rider.phoneNumber!!).get()
                                .addOnSuccessListener { document ->
                                    //getting list of phone numbers from firestore
                                    val phoneNumbers = document.get("contacts").toString()
                                    //list of the phone numbers to send the message to
                                    val phoneNumberList = phoneNumbers.split(",")
                                    //message to send
                                    val ref = FirebaseFirestore.getInstance()
                                    db1.collection("Driver").whereEqualTo("driverId", driverId)
                                        .get().addOnSuccessListener { documents ->
                                            documents.documents.forEach{
                                                val driver = it.toObject(Driver::class.java)
                                                val locationUrl = "https://maps.google.com/?q=$lat,$long"
                                                val driverCNIC = driver?.cnic
                                                val driverPhoneNumber = driver?.phoneNumber
                                                val driverLicenseNumber = driver?.licenseNumber
                                                val message1 = "I am in danger. Please HELP ME!\nInformation:\n" +
                                                        "Driver CNIC : $driverCNIC "
                                                val message2 = "Driver Phone Number : $driverPhoneNumber \n" +
                                                        "Vehicle License Number : $driverLicenseNumber \n"
                                                val message3 = "Driver Current Location : $locationUrl \n"
                                                //sending message here to all the numbers
                                                for (number in phoneNumberList){
                                                    val sentPI: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent("SMS_SENT"), 0)
                                                    SmsManager.getDefault().sendTextMessage(number, null, message1, sentPI, null)
                                                    SmsManager.getDefault().sendTextMessage(number, null, message2, sentPI, null)
                                                    SmsManager.getDefault().sendTextMessage(number, null, message3, sentPI, null)
                                                }
                                            }
                                        }

                                }
                        }
                    }
                }

            }
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
        }
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

//        try {
//            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
//            if(!success)
//                Log.e("ERROR", "Style parsing error")
//        }catch (e: Resources.NotFoundException){
//            Log.e("ERROR", e.message!!)
//        }

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

        val db = FirebaseDatabase.getInstance().getReference("RideSessions")
        db.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val session = snapshot.getValue(RideSession::class.java)
                    if(session!!.riderId == FirebaseAuth.getInstance().currentUser!!.uid){
                        var state = session.rideState
                        selectedPlaceEvent = SelectedPlaceEvent(origin = LatLng(session.driverLat,session.driverLng),
                            destination = LatLng(session.pickUpLocation!!["Lat"].toString().toDouble(), session.pickUpLocation!!["Lng"].toString().toDouble()))


                        marker?.remove()
                        marker = mMap.addMarker(MarkerOptions()
                            .position(selectedPlaceEvent.origin)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car)))


                        if(state == "In_Session" || state == "Reached"){
                            selectedPlaceEvent.destination = LatLng(session.dropOffLocation?.get("Lat") as Double,
                                session.dropOffLocation?.get("Lng") as Double)
                        }

                        if(session.midPointFlag && state == "In_Session"){
                            var places = SelectedPlaceEvent(origin = selectedPlaceEvent.origin,
                            destination = LatLng(session.midPoint?.get("Lat") as Double,
                                session.midPoint?.get("Lng") as Double))
                            drawPath2(places,state)
                            places.origin = places.destination
                            places.destination = selectedPlaceEvent.destination
                            drawPath(places,state)

                            mMap.addMarker(MarkerOptions()
                                .position(places.origin)
                                .flat(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)))
                        }
                        else{
                            greyPolylineMid?.remove()
                            blackPolyLineMid?.remove()
                            drawPath(selectedPlaceEvent, state)
                        }
                    }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val session = snapshot.getValue(RideSession::class.java)
                if(session!!.riderId == FirebaseAuth.getInstance().currentUser!!.uid){

                    val d = FirebaseDatabase.getInstance().getReference("Ride History")
                    val id = d.push()
                    id.setValue(session)

                    val intent = Intent(this@RiderRideSessionActivity, RiderMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun drawPath(selectedPlace:SelectedPlaceEvent, state:String) {
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
                    greyPolyline?.remove()
                    blackPolyLine?.remove()

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
                    }


                    val latLngBound = LatLngBounds.Builder().include(selectedPlace.origin)
                        .include(selectedPlace.destination)
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
                        pickUpMarker = mMap.addMarker(MarkerOptions()
                            .position(selectedPlace.destination)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)))
                    }

                    if(state == "In_Session"){
                        destMarker = mMap.addMarker(MarkerOptions()
                            .position(selectedPlace.destination)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)))
                    }

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

    private fun drawPath2(selectedPlace:SelectedPlaceEvent, state:String) {
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
                        polylineListMid = Common.decodePoly(polyline)
                    }
                    greyPolylineMid?.remove()
                    blackPolyLineMid?.remove()

                    if(state != "Waiting" || state != "Reached"){
                        polylineOptionsMid = PolylineOptions()
                        polylineOptionsMid!!.color(Color.parseColor("#E87C35"))
                        polylineOptionsMid!!.width(12f)
                        polylineOptionsMid!!.startCap(SquareCap())
                        polylineOptionsMid!!.jointType(JointType.ROUND)
                        polylineOptionsMid!!.addAll(polylineListMid!!)
                        greyPolylineMid = mMap.addPolyline(polylineOptionsMid!!)

                        blackPolylineOptionsMid = PolylineOptions()
                        blackPolylineOptionsMid!!.color(Color.parseColor("#fa8f5a"))
                        blackPolylineOptionsMid!!.width(5f)
                        blackPolylineOptionsMid!!.startCap(SquareCap())
                        blackPolylineOptionsMid!!.jointType(JointType.ROUND)
                        blackPolylineOptionsMid!!.addAll(polylineListMid!!)
                        blackPolyLineMid = mMap.addPolyline(blackPolylineOptionsMid!!)

                        //Animator
                        val valueAnimator = ValueAnimator.ofInt(0,100)
                        valueAnimator.duration = 1100
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        valueAnimator.interpolator = LinearInterpolator()
                        valueAnimator.addUpdateListener { value->
                            val points = greyPolylineMid!!.points
                            val percentValue = value.animatedValue.toString().toInt()
                            val size = points.size
                            val newPoints = (size * (percentValue/100.0f)).toInt()
                            val p = points.subList(0, newPoints)
                            blackPolyLineMid!!.points = p
                        }
                        valueAnimator.start()
                    }


                    val latLngBound = LatLngBounds.Builder().include(selectedPlace.origin)
                        .include(selectedPlace.destination)
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