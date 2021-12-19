package com.example.swift.frontEnd.adaptors

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import com.example.swift.R


class rideRequestListAdaptor( private ArrayList<RideRequest> rideRequestList = new ArrayList<RideRequest>()) : RecyclerView.Adapter<rideRequestListAdaptor.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var riderName = view.findViewById<>()

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): rideRequestListAdaptor.ViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.request_list_item, parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: rideRequestListAdaptor.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}