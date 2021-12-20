package com.example.swift.frontEnd.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.DriverOffer

class DriverOfferListAdapter (private val driversOfferList:  ArrayList<DriverOffer>) : RecyclerView.Adapter<DriverOfferListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name : TextView = view.findViewById(R.id.driversOffers_Name)
        var rating : TextView = view.findViewById(R.id.driversOffers_rating)
        var text: TextView = view.findViewById(R.id.driversOffers_Text)
        var chatBtn : Button = view.findViewById(R.id.driversOffers_chat_btn)
        var hideBtn : Button = view.findViewById(R.id.driversOffers_cancel_btn)

        // TODO: also load the profile picture of the rider from the url present in the driver offer

        init {
            chatBtn.setOnClickListener {
                TODO("Chat intent later")
            }

            hideBtn.setOnClickListener{
                val position : Int = adapterPosition
                driversOfferList.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DriverOfferListAdapter.ViewHolder {
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