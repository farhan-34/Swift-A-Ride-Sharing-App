package com.example.swift.frontEnd.rider.emergencyRide

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonParser {
    private fun parseJsonObject(obj: JSONObject): HashMap<String,String>{
        var map = HashMap<String,String>()
        try {
            val name = obj.getString("name")
            val lat = obj.getJSONObject("geometry")
                .getJSONObject("location")
                .getString("lat")
            val lng = obj.getJSONObject("geometry")
                .getJSONObject("location")
                .getString("lng")

            map["name"] = name
            map["lat"] = lat
            map["lng"] = lng
        }catch (e: JSONException){
            e.printStackTrace()
        }
        return map
    }

    private fun parseJsonArray(array:JSONArray?):List<HashMap<String,String>>{
        var list = ArrayList<HashMap<String,String>>()

        if(array != null) {
            for (i in 0..array.length()) {
                try {
                    var item = parseJsonObject(array.get(i) as JSONObject)
                    list.add(item)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        return list
    }

    public fun parseResult(obj:JSONObject):List<HashMap<String,String>>{
        var jsonArray: JSONArray? = null
        try {
            jsonArray = obj.getJSONArray("results")
        }catch (e:JSONException){
            e.printStackTrace()
        }
        return parseJsonArray(jsonArray)
    }
}