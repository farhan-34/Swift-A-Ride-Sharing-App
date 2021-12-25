package com.example.swift.frontEnd.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.example.swift.R
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.databinding.ActivityDriverMainBinding.inflate


class RideRequestListAdapter(private val rideRequestList:  ArrayList<RideRequest>) : RecyclerView.Adapter<RideRequestListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var riderName_view : TextView = view.findViewById(R.id.rideRequest_Item_riderName)
        var sourceLocation_view : TextView = view.findViewById(R.id.rideRequest_sourceLocation)
        var destinationLocation_view : TextView = view.findViewById(R.id.rideRequest_destinationLocation)
        var riderRating_view : TextView = view.findViewById(R.id.rideRequest_riderRating)
        var hideBtn : Button = view.findViewById(R.id.riderRequest_Hide_btn)

        var riderID = ""



        init {

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
    ): RideRequestListAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_list_item, parent, false)


        //show popup to send offer
        val chatBtn : Button = view.findViewById(R.id.riderRequest_chat_btn)
        chatBtn.setOnClickListener {
            val window = PopupWindow(parent.context)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.popup_send_offer_layout, null)
            window.contentView = view
            window.isFocusable = true
            window.update()
            val sndOfferBtn = view.findViewById<Button>(R.id.popup_send_offer_btn)
            val cancelOffer = view.findViewById<Button>(R.id.popup_cancleOffer_btn)
            sndOfferBtn.setOnClickListener{

                Toast.makeText(parent.context, "Offer Sent", Toast.LENGTH_SHORT).show()
                window.dismiss()
            }
            cancelOffer.setOnClickListener{
                window.dismiss()
            }
            window.showAsDropDown(chatBtn)

        }


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