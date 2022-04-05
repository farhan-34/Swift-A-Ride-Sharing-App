package com.example.swift.frontEnd.activities

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.EventBus.SelectedPlaceEvent
import com.example.swift.databinding.ActivityRequestDriverBinding
import com.example.swift.frontEnd.Remote.IGoogleAPI
import com.example.swift.frontEnd.Remote.RetroFitClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.IOException

class RequestDriverActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRequestDriverBinding

    private lateinit var mapFragment:SupportMapFragment

    private var selectedPlaceEvent: SelectedPlaceEvent? = null

    //Routes
    private val compositeDisposable = CompositeDisposable()
    private lateinit var iGoogleAPI: IGoogleAPI
    private var blackPolyLine: Polyline?=null
    private var greyPolyline:Polyline?= null
    private var polylineOptions: PolylineOptions?=null
    private var blackPolyline0ptions:PolylineOptions?=null
    private var polylineList:ArrayList<LatLng>? = null
    private var originMarker: Marker?= null
    private var destinationMarker:Marker?=null

    override fun onStart() {
//        if(!EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().register(this)
//        }
        super.onStart()
    }

    override fun onStop() {
        compositeDisposable.clear()
//        if(EventBus.getDefault().hasSubscriberForEvent(SelectedPlaceEvent::class.java))
//        {
//            EventBus.getDefault().removeStickyEvent(SelectedPlaceEvent::class.java)
//        }
//        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.request_driver_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun init(){
        iGoogleAPI = RetroFitClient.instance!!.create(IGoogleAPI::class.java)
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
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMyLocationClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(selectedPlaceEvent!!.origin.latitude,
                selectedPlaceEvent!!.origin.longitude),18f))
            true
        }


        //drawing path
        drawPath(selectedPlaceEvent!!)

        //Layout Button
        val locationButton = (mapFragment.requireView()!!
            .findViewById<View>("1".toInt())!!.parent!! as View)
            .findViewById<View>("2".toInt())
        val params = locationButton.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        params.bottomMargin = 250

        mMap.uiSettings.isZoomControlsEnabled = true
        try{
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style))
            if(!success)
                Snackbar.make(mapFragment.requireView(),"Map Loading Failed!", Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun drawPath(selectedPlaceEvent: SelectedPlaceEvent?) {
        compositeDisposable.add(iGoogleAPI.getDirections("driving","less_driving",selectedPlaceEvent!!.originString, selectedPlaceEvent.destinationString,
            getString(R.string.google_maps_key))!!.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(){ result ->
                Log.d("API_RETURN",result)
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray = jsonObject.getJSONArray("routes")
                    for (i in 0 until jsonArray.length()) {
                        val route = jsonArray.getJSONObject(i)
                        val poly = route.getJSONObject("overview_polyline")
                        val polyline = poly.getString("points")
                        polylineList = ArrayList(Common.decodePoly(polyline))
                    }

                    polylineOptions = PolylineOptions()
                    polylineOptions!!.color(Color.GRAY)
                    polylineOptions!!.width(12f)
                    polylineOptions!!.startCap(SquareCap())
                    polylineOptions!!.jointType(JointType.ROUND)
                    polylineOptions!!.addAll(polylineList!!)
                    greyPolyline = mMap.addPolyline(polylineOptions!!)

                    blackPolyline0ptions = PolylineOptions()
                    blackPolyline0ptions!!.color(Color.GRAY)
                    blackPolyline0ptions!!.width(12f)
                    blackPolyline0ptions!!.startCap(SquareCap())
                    blackPolyline0ptions!!.jointType(JointType.ROUND)
                    blackPolyline0ptions!!.addAll(polylineList!!)
                    blackPolyLine = mMap.addPolyline(blackPolyline0ptions!!)


                    //TODO Animator and put car icon on origin for route, also add marker for origin and destination video#21
                    //Animator
                    val valueAnimator = ValueAnimator.ofInt(0, 100)
                    valueAnimator.duration = 1100
                    valueAnimator.repeatCount = ValueAnimator.INFINITE
                    valueAnimator.interpolator = LinearInterpolator()
                    valueAnimator.addUpdateListener { value ->
                        val points = greyPolyline!!.points
                        val percentValue = value.animatedValue.toString().toInt()
                        val size = points.size
                        val newpoints = (size * (percentValue / 100.0f)).toInt()
                        val p = points.subList(0, newpoints)
                        blackPolyLine!!.points = p
                        valueAnimator.start()

                  }
                }catch(e:IOException){
                    e.printStackTrace()
                }
            })
    }
}