package com.example.swift.frontEnd.driver.rideHistory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.dataClasses.Rider
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.frontEnd.driver.riderRequests.RideRequestListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class RideHistoryAdapter(private val rideHistory:  ArrayList<RideSession>) : RecyclerView.Adapter<RideHistoryAdapter.ViewHolder>() {




    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name : TextView = view.findViewById(R.id.driverHistory_riderName)
        var pickUp : TextView = view.findViewById(R.id.driverHistory_pickUp)
        var dropOff : TextView = view.findViewById(R.id.driverHistory_dropOff)
        var fare : TextView= view.findViewById(R.id.driverHistory_fare)
        var status: TextView=view.findViewById(R.id.driverHistory_status)
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.driver_ride_history_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rideHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        DriverSession.getCurrentUser {
            val docRef = db.collection("Rider").document(it.phoneNumber)
            docRef.get().addOnSuccessListener { doc ->
                val person = doc.toObject<Rider>()
                holder.name.text = person?.name
            }
        }

        holder.pickUp.text = rideHistory[position].pickUpLocation?.get("Address") as String
        holder.dropOff.text = rideHistory[position].dropOffLocation?.get("Address") as String
        holder.fare.text = rideHistory[position].money

        if(rideHistory[position].rideState == "Finished"){
            holder.status.text = "Complete"
        }
        else{
            holder.status.text = "Canceled"
            holder.status.setTextColor(Color.rgb(255,0,0))
        }
    }
}