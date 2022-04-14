package com.example.swift.frontEnd.rider.rideHistory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.dataClasses.Rider
import com.example.swift.businessLayer.session.RiderSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class RiderRideHistoryAdapter(private val rideHistory:  ArrayList<RideSession>) : RecyclerView.Adapter<RiderRideHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name : TextView = view.findViewById(R.id.riderHistory_riderName)
        var pickUp : TextView = view.findViewById(R.id.riderHistory_pickUp)
        var dropOff : TextView = view.findViewById(R.id.riderHistory_dropOff)
        var fare : TextView = view.findViewById(R.id.riderHistory_fare)
        var status: TextView =view.findViewById(R.id.riderHistory_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rider_history_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        RiderSession.getCurrentUser {
            val docRef = db.collection("Rider").document()
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

    override fun getItemCount(): Int {
        return rideHistory.size
    }
}