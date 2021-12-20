package com.example.swift.frontEnd.adaptors

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.swift.R
import com.example.swift.businessLayer.businessLogic.RideRequest


class RideRequestListAdaptor(private val rideRequestList:  ArrayList<RideRequest>) : RecyclerView.Adapter<RideRequestListAdaptor.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var riderName_view : TextView = view.findViewById(R.id.rideRequest_Item_riderName)
        var sourceLocation_view : TextView = view.findViewById(R.id.rideRequest_sourceLocation)
        var destinationLocation_view : TextView = view.findViewById(R.id.rideRequest_destinationLocation)
        var riderRating_view : TextView = view.findViewById(R.id.rideRequest_riderRating)
        var chatBtn : Button = view.findViewById(R.id.riderRequest_chat_btn)
        var hideBtn : Button = view.findViewById(R.id.riderRequest_Hide_btn)

        init {
            chatBtn.setOnClickListener {
                TODO("Chat intent later")
            }

            hideBtn.setOnClickListener{
                val position : Int = adapterPosition
                rideRequestList.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RideRequestListAdaptor.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.request_list_item, parent, false)
        return ViewHolder(view)


    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.riderName_view.text = rideRequestList[position].riderName
        viewHolder.riderRating_view.text = rideRequestList[position].riderRating
        viewHolder.sourceLocation_view.text = rideRequestList[position].sourceLocation
        viewHolder.destinationLocation_view.text = rideRequestList[position].destinationLocation
    }


    override fun getItemCount() = rideRequestList.size


}