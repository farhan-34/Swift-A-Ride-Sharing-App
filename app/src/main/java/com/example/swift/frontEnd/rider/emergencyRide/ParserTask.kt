package com.example.swift.frontEnd.rider.emergencyRide

import android.content.Context
import android.os.AsyncTask
import com.example.swift.businessLayer.Common.Common
import com.example.swift.frontEnd.rider.riderMain.RiderMainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject

class ParserTask : AsyncTask<String, Int, List<HashMap<String, String>>>() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun doInBackground(vararg p0: String?): List<HashMap<String, String>>? {
        var jsonParser = JsonParser()
        var mapList:List<HashMap<String,String>>? = null
        var jsonObject:JSONObject? = null
        try {
            jsonObject = JSONObject(p0[0])
            mapList = jsonParser.parseResult(jsonObject)
        }catch (e:JSONException){
            e.printStackTrace()
        }
        return mapList
    }

    override fun onPostExecute(result: List<HashMap<String, String>>?) {
        if(result!=null){
            Common.hospital = result
            for(map in result){
                val lat = map["lat"] as Double
                val lng = map["lng"] as Double
                val name = map["name"]
                val latLng = LatLng(lat, lng)

            }
//            for(i in 0..result.size){
//                var map = result[i]
//                val lat = map["lat"] as Double
//                val lng = map["lng"] as Double
//                val name = map["name"]
//                val latLng = LatLng(lat, lng)
//            }
            Common.endThread = true
        }
    }


}
