package com.example.swift.frontEnd.rider.homePage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.businessLayer.dataClasses.Driver
import com.example.swift.businessLayer.dataClasses.DriverGeo
import com.example.swift.businessLayer.dataClasses.GeoQueryModel
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.Callback.FirebaseDriverInfoListener
import com.example.swift.frontEnd.Callback.FirebaseFailedListener
import com.example.swift.frontEnd.Services.NotifyOnDriverOffer
import com.example.swift.frontEnd.rider.emergencyRide.JsonParser
import com.example.swift.frontEnd.rider.offers.OfferListActivity
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_rider_home_page.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*


class RiderHomePageFragment : Fragment(), OnMapReadyCallback, FirebaseDriverInfoListener,LocationListener,GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener,
GoogleMap.OnCameraMoveStartedListener{

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    //for places
    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment


    //location
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback

    //select location with pointer
    private lateinit var destination: LatLng

    //for places midpoint
    private lateinit var autocompleteSupportFragmentMid: AutocompleteSupportFragment
    private var midPoint: LatLng? = null
    private var midPointFlag:Boolean = false


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

    //vehicle type
    var vehicleType = "car"





    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        destination = LatLng(0.0, 0.0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rider_home_page, container, false)
        // Inflate the layout for this fragment

        initViews(root)
        return root
    }


    private fun initViews(root: View?) {
       // slidingUpPanelLayout = root!!.findViewById(R.id.sliding_layout) as SlidingUpPanelLayout
    }

    private fun init() {

        //setting up places
        Places.initialize(requireContext(),getString(R.string.google_maps_key))
        autocompleteSupportFragment = childFragmentManager.findFragmentById(R.id.rider_pickUpLocation_autoComplete) as AutocompleteSupportFragment
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        ))

        //TODO : remove Later
        autocompleteSupportFragment.setOnPlaceSelectedListener(object:PlaceSelectionListener{
            override fun onError(p0: Status) {

            }

            override fun onPlaceSelected(p0: Place) {
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        p0.latLng as LatLng,
                        18f
                    )
                )
                destination = p0.latLng as LatLng
            }

        })


        //for mid point
        autocompleteSupportFragmentMid = childFragmentManager.findFragmentById(R.id.rider_midPoint_autoComplete) as AutocompleteSupportFragment
        autocompleteSupportFragmentMid.setPlaceFields(Arrays.asList(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        ))

        autocompleteSupportFragmentMid.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onError(p0: Status) {

            }

            override fun onPlaceSelected(p0: Place) {
                midPoint = p0.latLng as LatLng
                midPointFlag = true
            }

        })


        iFirebaseDriverInfoListener = this

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 3000
        locationRequest.interval = 5000
        locationRequest.smallestDisplacement = 10f

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val newPos = LatLng(locationResult.lastLocation.latitude,locationResult.lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))

                //If the location of a user is changed then load all the drivers again
                if(firstTime){
                    currentLocation = locationResult.lastLocation
                    previousLocation = locationResult.lastLocation

                    firstTime = false

                    setRistrictedPlaces(locationResult!!.lastLocation)
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

    private fun setRistrictedPlaces(location: Location) {
        try {
            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            var addressList = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
            if(addressList.size>0)
                autocompleteSupportFragment.setCountry(addressList[0].countryCode)

        }catch (e:IOException){
            e.printStackTrace()
        }

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
                try {
                    if(location!= null) {
                        addressList =
                            geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    }
                    if (addressList.isNotEmpty())
                        cityName = addressList[0].locality

                    //Query
                    if (!TextUtils.isEmpty(cityName)) {
                        val driverLocationRef = FirebaseDatabase.getInstance()
                            .getReference("DriversLocation")
                            .child(cityName)
                        val gf = GeoFire(driverLocationRef)
                        val geoQuery = gf.queryAtLocation(
                            GeoLocation(location.latitude, location.longitude),
                            distance
                        )
                        geoQuery.removeAllListeners()

                        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
                            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                                //Common.driversFound.add(DriverGeo(key!!, location!!))
                                if(!Common.driversFound.containsKey(key!!)){
                                    Common.driversFound[key] = DriverGeo(key,location)
                                }
                            }

                            override fun onKeyExited(key: String?) {

                            }

                            override fun onKeyMoved(key: String?, location: GeoLocation?) {

                            }

                            override fun onGeoQueryReady() {
                                if (distance <= limitRange) {
                                    distance++
                                    loadAvailableDrivers()
                                } else {
                                    distance = 0.0
                                    addDriverMarker()
                                }
                            }

                            override fun onGeoQueryError(error: DatabaseError?) {
                                Snackbar.make(requireView(), error!!.message, Snackbar.LENGTH_SHORT)
                                    .show()
                            }

                        })

                        driverLocationRef.addChildEventListener(object : ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                var geoQueryModel = snapshot.getValue(GeoQueryModel::class.java)
                                val geoLocation =
                                    GeoLocation(geoQueryModel!!.l!![0], geoQueryModel!!.l!![1])
                                val driverGeo = DriverGeo(snapshot.key, geoLocation)
                                val newDriverLocation = Location("")
                                newDriverLocation.latitude = geoLocation.latitude
                                newDriverLocation.longitude = geoLocation.longitude
                                val newDistance =
                                    location.distanceTo(newDriverLocation) / 1000  //in km
                                if (newDistance <= limitRange)
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
                                Snackbar.make(requireView(), error.message, Snackbar.LENGTH_SHORT)
                                    .show()
                            }

                        })

                    }
                }
                catch(e:IOException){
                    e.printStackTrace()
                }
            }

    }

    private fun addDriverMarker() {

        if(Common.driversFound.size > 0){
            io.reactivex.rxjava3.core.Observable.fromIterable(Common.driversFound.keys)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {key:String ->
                        findDriverByKey(Common.driversFound[key!!])
                    },
                    {
                        t: Throwable? -> Snackbar.make(requireView(), t!!.message!!,Snackbar.LENGTH_SHORT).show()
                    }
                )
        }else{
            //Snackbar.make(requireView(), "Driver not found",Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun findDriverByKey(driverGeoModel: DriverGeo?) {
        FirebaseFirestore.getInstance().collection("Driver")
            .whereEqualTo("driverId", driverGeoModel!!.key)
            .get().addOnSuccessListener{ docs ->
                if (docs.documents.size>0){
                    driverGeoModel.driverInfo = docs.documents[0].toObject(Driver::class.java)
                    Common.driversFound[driverGeoModel.key]!!.driverInfo = docs.documents[0].toObject(Driver::class.java)
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
        init()

        ride_request.setOnClickListener {
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
                return@setOnClickListener
            }
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener {location ->
                val origin = LatLng(location.latitude, location.longitude)
                //startActivity(Intent(requireContext(), RequestDriverActivity::class.java))

                //EventBus.getDefault().postSticky(SelectedPlaceEvent(origin,destination))

                broadcastRequest(origin)
                val intent = Intent(requireContext(), NotifyOnDriverOffer::class.java)
                Common.endThread = false
                requireContext().startService(intent)
                //parentFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderOfferListFragment()).addToBackStack(null).commit()
                startActivity(Intent(requireContext(), OfferListActivity::class.java))
            }
        }

        //for emergency ride
        emergency_ride_button.bringToFront()



        //vehicle type

        car_selected.setOnClickListener {
            car_selected.setBackgroundColor(Color.parseColor("#E87C35"))
            vehicleType = "car"
            bike_selected.setBackgroundColor(Color.parseColor("#727272"))
        }

        bike_selected.setOnClickListener {
            car_selected.setBackgroundColor(Color.parseColor("#727272"))
            vehicleType = "bike"
            bike_selected.setBackgroundColor(Color.parseColor("#E87C35"))
        }



        /*
        ************************************
               Emergency Ride function
        ************************************
        */

        emergency_ride_button.setOnClickListener {
            val url =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=" + currentLocation!!.latitude + "," + currentLocation!!.longitude +
                        "&radius=5000" +
                        "&type=" + "hospital"+
                        "&sensor=true" + "&key=" + resources.getString(R.string.google_maps_key)

            //PlaceTask().execute(url)
            var data = ""
            var jsonParser = JsonParser()
            var mapList:List<HashMap<String,String>>? = null
            var jsonObject: JSONObject? = null

            val thread = Thread {
                try {
                    var url = URL(url)
                    var connection = url.openConnection()
                    connection.connect()
                    var stream = connection.getInputStream()
                    var reader = BufferedReader(InputStreamReader(stream))
                    var builder = StringBuilder()
                    var line: String? = ""

                    while (line != null) {
                        line = reader.readLine()
                        if (line != null) {
                            builder.append(line)
                        }
                    }
                    data = builder.toString()
                    reader.close()

                    jsonObject = JSONObject(data)
                    mapList = jsonParser.parseResult(jsonObject!!)

                    var minDistance = 100000.0
                    var hospitalLocation:LatLng? = null

                    for(i in 0..mapList!!.size / 2){
                        var m = mapList!![i]
                        var temp = LatLng(m["lat"]!!.toDouble(), m["lng"]!!.toDouble())
                        var dist = SphericalUtil.computeDistanceBetween(
                            LatLng(currentLocation!!.latitude, currentLocation!!.longitude),
                            temp)

                        dist /= 1000

                        if(dist < minDistance){
                            minDistance = dist
                            hospitalLocation = temp
                        }
                    }

                    if(hospitalLocation!= null){
                        destination = hospitalLocation
                    }
                    else{
                        return@Thread
                    }

                    if(Common.driversFound.isNotEmpty()){
                        var maxDistance = 10.0
                        var riderLocation = Location("")
                        riderLocation.latitude = currentLocation!!.latitude
                        riderLocation.longitude = currentLocation!!.longitude
                        var driverLocation = Location("")

                        var minDistanceToDriver = 10000.0
                        var pickup = mutableMapOf<String, Any>()
                        var dropOff = mutableMapOf<String, Any>()
                        var mid: MutableMap<String, Any>? = null
                        var driverKey:String? = null
                        var driverLoc:Location = Location("")

                        for(key in Common.driversFound.keys){
                            driverLocation.latitude = Common.driversFound[key]!!.geoLocation!!.latitude
                            driverLocation.longitude = Common.driversFound[key]!!.geoLocation!!.longitude
                            if(driverLocation.distanceTo(riderLocation)/1000 < maxDistance){
                                if(driverLocation.distanceTo(riderLocation)/1000 < minDistanceToDriver){
                                    minDistanceToDriver = (driverLocation.distanceTo(riderLocation)/1000).toDouble()
                                    driverKey = key
                                    driverLoc = driverLocation
                                }
                                pickup = mutableMapOf<String, Any>()
                                dropOff = mutableMapOf<String, Any>()
                                var mid: MutableMap<String, Any>? = null
                                //var ride = Ride("",FirebaseAuth.getInstance().currentUser!!.uid,key,pickup,dropOff,"pending", 100)
                                //ref.push().setValue(ride)
                            }
                        }
                        try {
                            var address = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(
                                currentLocation!!.latitude, currentLocation!!.longitude, 1)
                            pickup["Lat"] = currentLocation!!.latitude
                            pickup["Lng"] = currentLocation!!.longitude
                            pickup["Address"] = address[0].getAddressLine(0)
                            address = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(destination.latitude,destination.longitude, 1)
                            dropOff["Lat"] = destination.latitude
                            dropOff["Lng"] = destination.longitude
                            dropOff["Address"] = address[0].getAddressLine(0)
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                        midPointFlag = false
                        if(driverKey!= null) {
                            RiderSession.getCurrentUser { rider ->
                                var session: RideSession = RideSession( offerId = "_offerId",
                                    driverId = driverKey,
                                    riderId = rider.riderId,
                                    rideState = "Picking_Up",
                                    pickUpLocation = pickup,
                                    dropOffLocation = dropOff,
                                    driverLat = driverLoc.latitude,
                                    driverLng = driverLoc.longitude,
                                    vehicleType = "car",
                                    money = getString(R.string.EmergencyRideRate),
                                    midPoint= mid,
                                    midPointFlag = midPointFlag)

                                var db = FirebaseDatabase.getInstance().getReference("RideSessions")
                                val offerId = db.push()
                                session.offerId = offerId.key.toString()
                                offerId.setValue(session).addOnSuccessListener {
                                    Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                        else{
                            Toast.makeText(requireContext(), "Driver Not Found", Toast.LENGTH_SHORT).show()
                            return@Thread
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()

        }
    }

    private fun broadcastRequest(origin: LatLng) {
        var ref = FirebaseDatabase.getInstance().getReference("RideRequests")
        if(Common.driversFound.isNotEmpty()){
            var maxDistance = 10.0
            var riderLocation = Location("")
            riderLocation.latitude = origin.latitude
            riderLocation.longitude = origin.longitude
            var driverLocation = Location("")
            for(key in Common.driversFound.keys){
                driverLocation.latitude = Common.driversFound[key]!!.geoLocation!!.latitude
                driverLocation.longitude = Common.driversFound[key]!!.geoLocation!!.longitude
                if(driverLocation.distanceTo(riderLocation)/1000 < maxDistance){
                    var pickup = mutableMapOf<String, Any>()
                    var dropOff = mutableMapOf<String, Any>()
                    var mid: MutableMap<String, Any>? = null
                    try {
                        var address = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(origin.latitude,origin.longitude, 1)
                        pickup["Lat"] = origin.latitude
                        pickup["Lng"] = origin.longitude
                        pickup["Address"] = address[0].getAddressLine(0)
                        address = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(destination.latitude,destination.longitude, 1)
                        dropOff["Lat"] = destination.latitude
                        dropOff["Lng"] = destination.longitude
                        dropOff["Address"] = address[0].getAddressLine(0)
                        if(midPoint != null) {
                            mid = mutableMapOf<String, Any>()
                            address =
                                Geocoder(requireContext(), Locale.getDefault()).getFromLocation(
                                    midPoint!!.latitude,
                                    midPoint!!.longitude,
                                    1
                                )
                            mid["Lat"] = midPoint!!.latitude
                            mid["Lng"] = midPoint!!.longitude
                            mid["Address"] = address[0].getAddressLine(0)
                        }
                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                    RiderSession.getCurrentUser { rider ->
                        var riderRequest = RideRequest("",FirebaseAuth.getInstance().currentUser!!.uid,key,rider.name,rider.rating,pickup,dropOff,vehicleType,mid,midPointFlag)
                        val requestId = ref.push()
                        riderRequest.requestId = requestId.key.toString()
                        requestId.setValue(riderRequest)
                    }
                    //var ride = Ride("",FirebaseAuth.getInstance().currentUser!!.uid,key,pickup,dropOff,"pending", 100)
                    //ref.push().setValue(ride)
                }
            }
        }
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


//        try {
//            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
//            if(!success)
//                Log.e("EDMT_ERROR", "Style parsing error")
//        }catch (e:Resources.NotFoundException){
//            Log.e("EDMT_ERROR", e.message!!)
//        }

        //mMap.isMyLocationEnabled = true
        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveStartedListener(this)
        mMap.setOnCameraMoveListener(this)

    }

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        mapFragment.getMapAsync(this)
        //init()
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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car)))!!)

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


    override fun onLocationChanged(location: Location) {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        var address : List<Address>? = null
        try{
            address = geoCoder.getFromLocation(location.latitude,location.longitude,1)
        }catch (e:IOException){
            e.printStackTrace()
        }
        setAddress(address!![0])
    }

    private fun setAddress(address: Address) {
        destination = LatLng(address.latitude, address.longitude)
        //Toast.makeText(requireContext(), "${destination.longitude} + and +${destination.latitude}",Toast.LENGTH_SHORT).show()
    }

    override fun onCameraIdle() {
        var address: List<Address>? = null
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            address = geoCoder.getFromLocation(mMap.cameraPosition.target.latitude, mMap.cameraPosition.target.longitude, 1)
            setAddress(address!![0])
        }catch (e:IndexOutOfBoundsException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    override fun onCameraMove() {

    }

    override fun onCameraMoveStarted(p0: Int) {

    }
}


