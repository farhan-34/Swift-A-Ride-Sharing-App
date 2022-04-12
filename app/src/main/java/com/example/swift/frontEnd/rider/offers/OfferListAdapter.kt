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
import com.example.swift.frontEnd.driver.riderRequests.RideRequestListAdapter
import com.example.swift.frontEnd.rider.chat.RiderChatLogActivity
import com.example.swift.frontEnd.rider.rideSession.RiderRideSessionActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OfferListAdapter (var context:Context, private val driversOfferList:  ArrayList<DriverOffer>) : RecyclerView.Adapter<OfferListAdapter.ViewHolder>() {

    companion object {
        val OFFER_KEY = "OFFER_KEY"
    }

    var index = 0


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

                var offer = driversOfferList[index]

                val intent = Intent(view.context, RiderChatLogActivity::class.java)
//                  intent.putExtra(USER_KEY,  userItem.user.username)
                val nullRequest : RideRequest? = null
                intent.putExtra(RideRequestListAdapter.REQUEST_KEY, nullRequest)
                intent.putExtra(OFFER_KEY, offer)

                context.startActivity(intent)


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
                //TODO: remove other offers and Request as well

                var db = FirebaseDatabase.getInstance().reference.child("RideRequests")


                val position: Int = adapterPosition
                var pickUpLocation:MutableMap<String, Any>
                var dropOffLocation:MutableMap<String, Any>

               db.get().addOnSuccessListener {
                   for (child in it.children) {
                       val req = child.getValue(RideRequest::class.java)
                       if (req!!.driverId == driversOfferList[position].driverId) {
                           pickUpLocation = req.sourceLocation!!
                           dropOffLocation = req.destinationLocation!!

                           val latLng = LatLng(Common.driversFound[driversOfferList[position].driverId]!!.geoLocation!!.latitude,
                               Common.driversFound[driversOfferList[position].driverId]!!.geoLocation!!.longitude)

                           val d = FirebaseDatabase.getInstance().getReference("RiderOffers")
                           var money = ""
                           d.get().addOnSuccessListener { i ->
                               for (ch in i.children){
                                   val offer = ch.getValue(DriverOffer::class.java)
                                   if (req.driverId == offer?.driverId){
                                       money = offer!!.text
                                       var session: RideSession = RideSession( offerId = driversOfferList[position].offerId,
                                           driverId = driversOfferList[position].driverId,
                                           riderId = driversOfferList[position].riderId,
                                           rideState = "Picking_Up",
                                           pickUpLocation = pickUpLocation,
                                           dropOffLocation = dropOffLocation,
                                           driverLat = latLng.latitude,
                                           driverLng = latLng.longitude,
                                           vehicleType = req.vehicleType,
                                           money = money)


                                       db = FirebaseDatabase.getInstance().getReference("RideSessions")
                                       val offerId = db.push()
                                       session.offerId = offerId.key.toString()
                                       offerId.setValue(session)

                                       val intent = Intent(context, RiderRideSessionActivity::class.java)
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                       context.startActivity(intent)
                                       break
                                   }
                               }
                               db = FirebaseDatabase.getInstance().reference
                               val query1 = db.child("RideRequests")
                                   .orderByChild("riderId")
                                   .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)

                               query1.addListenerForSingleValueEvent(object : ValueEventListener {
                                   override fun onDataChange(dataSnapshot: DataSnapshot) {
                                       for (docs in dataSnapshot.children) {
                                           docs.ref.removeValue()
                                       }

                                   }

                                   override fun onCancelled(databaseError: DatabaseError) {
                                       Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                                   }
                               })

                               //removing existing offers for current rider
                               val query2 = db.child("RiderOffers")
                                   .orderByChild("riderId")
                                   .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)

                               query2.addListenerForSingleValueEvent(object : ValueEventListener {
                                   override fun onDataChange(dataSnapshot: DataSnapshot) {
                                       for (docs in dataSnapshot.children) {
                                           docs.ref.removeValue()
                                       }

                                   }

                                   override fun onCancelled(databaseError: DatabaseError) {
                                       Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                                   }
                               })
                           }

                       }
                   }

               }

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

        index = position
    }


    override fun getItemCount() = driversOfferList.size


}