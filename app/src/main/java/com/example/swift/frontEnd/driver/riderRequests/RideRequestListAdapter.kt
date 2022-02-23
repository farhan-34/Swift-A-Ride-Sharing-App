package com.example.swift.frontEnd.driver.riderRequests

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.widget.*
import com.example.swift.R
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.businessLayer.session.RiderSession
import com.google.firebase.database.FirebaseDatabase


class RideRequestListAdapter(private val rideRequestList:  ArrayList<RideRequest>) : RecyclerView.Adapter<RideRequestListAdapter.ViewHolder>() {

    var riderId:String = ""
    var offerFair:String = ""

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var riderName_view : TextView = view.findViewById(R.id.rideRequest_Item_riderName)
        var sourceLocation_view : TextView = view.findViewById(R.id.rideRequest_sourceLocation)
        var destinationLocation_view : TextView = view.findViewById(R.id.rideRequest_destinationLocation)
        var riderRating_view : TextView = view.findViewById(R.id.rideRequest_riderRating)
        var hideBtn : Button = view.findViewById(R.id.riderRequest_Hide_btn)
        val db = FirebaseDatabase.getInstance()


        init {

            hideBtn.setOnClickListener{
//                val position : Int = adapterPosition
//                rideRequestList.removeAt(position)
//                notifyItemRemoved(position)
                //var db = FirebaseDatabase.getInstance().reference.child("RideRequests")

                //getting position for the offer
                val position : Int = adapterPosition

                //driversOfferList.removeAt(position)
                //driversOfferList.removeAt(position)
                //notifyItemRemoved(position)
                //notifyDataSetChanged()

                //removing value from database
                rideRequestList[position].requestId?.let { it1 -> db.reference.child("RideRequests").child(it1).removeValue() }


//                val driverId = rideRequestList[position].driverId
//                val riderId = rideRequestList[position].riderId
//                val query1 = db.reference.child("RiderOffers")
//                    .equalTo(driverId, "driverId")
//                    //.equalTo(riderId, "riderId")
//
//                query1.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        for (docs in dataSnapshot.children) {
//                            docs.children.forEach{
//                            if(riderId == docs.child("riderId").toString())
//                                FirebaseDatabase.getInstance().getReference("RiderOffers/${docs.key}").removeValue()
//                            }
//                        }
//                    }
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
//                    }
//                })
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
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
            window.showAtLocation(parent, Gravity.CENTER, 0 , 0);
            window.isOutsideTouchable = false;
            window.update()
            val sndOfferBtn = view.findViewById<Button>(R.id.popup_send_offer_btn)
            val cancelOffer = view.findViewById<Button>(R.id.popup_cancleOffer_btn)
            sndOfferBtn.setOnClickListener{
                //check validation
                val temp: EditText = view.findViewById(R.id.popup_offer_price_view)
                val fair = temp.text.toString()
                if(isPositiveNumber(fair)) {
                    val obj = DriverOffer()
                    //getting current driver
                    RiderSession.getCurrentUser { rider ->
                        DriverSession.getCurrentUser { driver ->
                            obj.driverId = driver.driverId
                            obj.name = rider.name
                            obj.riderId = riderId
                            obj.text = fair
                            val db = FirebaseDatabase.getInstance().getReference("RiderOffers")
                            val offerId = db.push()
                            obj.offerId = offerId.key.toString()
                            offerId.setValue(obj)
                        }
                    }
                    Toast.makeText(parent.context, "Offer Sent", Toast.LENGTH_SHORT).show()
                    window.dismiss()

                }else {
                    Toast.makeText(parent.context, "Price not valid", Toast.LENGTH_SHORT).show()
                }
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
        viewHolder.riderRating_view.text = rideRequestList[position].riderRating.toString()
        viewHolder.sourceLocation_view.text = rideRequestList[position].sourceLocation
        viewHolder.destinationLocation_view.text = rideRequestList[position].destinationLocation
        riderId = rideRequestList[position].riderId.toString()
    }


    override fun getItemCount() = rideRequestList.size


    //fun to check whether the string is a positive number or not
    fun isPositiveNumber(string: String): Boolean {

        var numeric = true

        numeric = string.matches("0?\\d+(\\.\\d+)?".toRegex())
        return numeric
    }

}