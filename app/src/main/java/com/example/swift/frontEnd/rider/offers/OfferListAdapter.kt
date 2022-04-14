package com.example.swift.frontEnd.rider.offers

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.driver.riderRequests.RideRequestListAdapter
import com.example.swift.frontEnd.rider.chat.RiderChatLogActivity
import com.example.swift.frontEnd.rider.rideSession.RiderRideSessionActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OfferListAdapter (private val driversOfferList:  ArrayList<DriverOffer>) : RecyclerView.Adapter<OfferListAdapter.ViewHolder>() {

    companion object {
        val OFFER_KEY = "OFFER_KEY"
        val IS_RIDER = "IS_RIDER"
    }




    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name : TextView = view.findViewById(R.id.driversOffers_Name)
        var rating : TextView = view.findViewById(R.id.driversOffers_rating)
        var text: TextView = view.findViewById(R.id.driversOffers_Text)
        var chatBtn : Button = view.findViewById(R.id.driversOffers_chat_btn)
        var hideBtn : Button = view.findViewById(R.id.driversOffers_cancel_btn)
        var acceptBtn : Button = view.findViewById(R.id.driversOffers_accept_btn)

        // TODO: also load the profile picture of the rider from the url present in the driver offer

        init {
            chatBtn.setOnClickListener {

                val position : Int = adapterPosition
                var offer = driversOfferList[position]

                val intent = Intent(view.context, RiderChatLogActivity::class.java)
//                  intent.putExtra(USER_KEY,  userItem.user.username)
                val nullRequest : RideRequest? = null
                intent.putExtra(RideRequestListAdapter.REQUEST_KEY, nullRequest)
                intent.putExtra(OFFER_KEY, offer)
                intent.putExtra(IS_RIDER, true)

                view.context.startActivity(intent)


            }


            hideBtn.setOnClickListener{
                var db = FirebaseDatabase.getInstance().getReference().child("RiderOffers")

                //getting position for the offer
                val position : Int = adapterPosition

                //driversOfferList.removeAt(position)
                //driversOfferList.removeAt(position)
                //notifyItemRemoved(position)
                //notifyDataSetChanged()

                //removing value from database
                db.child(driversOfferList[position].offerId).removeValue()
            }

            acceptBtn.setOnClickListener{
                val obj = AcceptOfferAction()
                val position: Int = adapterPosition

                val offerDriverID = driversOfferList[position].driverId
                val offerRiderID = driversOfferList[position].riderId
                val offerID = driversOfferList[position].offerId
                obj.acceptOffer(offerDriverID,offerRiderID,offerID,  view.context)
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.drivers_offers_list_item, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = driversOfferList[position].driverName
        viewHolder.rating.text = driversOfferList[position].rating.toString()
        viewHolder.text.text = driversOfferList[position].text


    }


    override fun getItemCount() = driversOfferList.size


}