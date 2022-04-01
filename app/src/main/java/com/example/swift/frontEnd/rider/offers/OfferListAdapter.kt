package com.example.swift.frontEnd.rider.offers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.Driver
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.dataClasses.Rider
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.rider.chat.RiderChatLogActivity
import com.google.firebase.database.FirebaseDatabase

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


                DriverSession.getCurrentUser { driver ->

                    var offer = driversOfferList[index]

                    val intent = Intent(view.context, RiderChatLogActivity::class.java)
//                  intent.putExtra(USER_KEY,  userItem.user.username)
                    intent.putExtra(OFFER_KEY, offer)

                    context.startActivity(intent)

                }



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
                var pickUpLocation:String  = ""
                var dropOffLocation: String = ""

               db.get().addOnSuccessListener {
                   for (child in it.children) {
                       if (child.child("driverId").value == driversOfferList[position].driverId) {
                           pickUpLocation = child.child("sourceLocation").value.toString()
                           dropOffLocation = child.child("destinationLocation").value.toString()

                           var session: RideSession = RideSession( offerId = driversOfferList[position].offerId,
                               driverId = driversOfferList[position].driverId,
                               riderId = driversOfferList[position].riderId,
                               rideState = "Picking_Up",
                               pickUpLocation = pickUpLocation,
                               dropOffLocation = dropOffLocation)


                           db = FirebaseDatabase.getInstance().getReference("RideSessions")
                           val offerId = db.push()
                           session.offerId = offerId.key.toString()
                           offerId.setValue(session)

                           break

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
        viewHolder.name.text = driversOfferList[position].name
        viewHolder.rating.text = driversOfferList[position].rating.toString()
        viewHolder.text.text = driversOfferList[position].text

        index = position
    }


    override fun getItemCount() = driversOfferList.size


}