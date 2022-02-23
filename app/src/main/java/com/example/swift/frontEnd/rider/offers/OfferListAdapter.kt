package com.example.swift.frontEnd.rider.offers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.google.firebase.database.FirebaseDatabase

class OfferListAdapter (var context:Context, private val driversOfferList:  ArrayList<DriverOffer>) : RecyclerView.Adapter<OfferListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name : TextView = view.findViewById(R.id.driversOffers_Name)
        var rating : TextView = view.findViewById(R.id.driversOffers_rating)
        var text: TextView = view.findViewById(R.id.driversOffers_Text)
        var chatBtn : Button = view.findViewById(R.id.driversOffers_chat_btn)
        var hideBtn : Button = view.findViewById(R.id.driversOffers_cancel_btn)

        // TODO: also load the profile picture of the rider from the url present in the driver offer

        init {
            chatBtn.setOnClickListener {
                //TODO: Ride Session
                Toast.makeText(context,"Ride Session will start here!!",Toast.LENGTH_SHORT).show()
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
    }


    override fun getItemCount() = driversOfferList.size


}