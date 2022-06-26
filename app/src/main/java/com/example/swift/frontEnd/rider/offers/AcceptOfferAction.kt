package com.example.swift.frontEnd.rider.offers

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.rider.rideSession.RiderRideSessionActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AcceptOfferAction {
    public fun acceptOffer(offerDriverId: String, offerRiderId: String, _offerId:String, context: Context ){
        var db = FirebaseDatabase.getInstance().reference.child("RideRequests")



        var pickUpLocation:MutableMap<String, Any>
        var dropOffLocation:MutableMap<String, Any>

        db.get().addOnSuccessListener {
            for (child in it.children) {
                val req = child.getValue(RideRequest::class.java)
                if (req!!.riderId == offerRiderId) {
                    pickUpLocation = req.sourceLocation!!
                    dropOffLocation = req.destinationLocation!!

                    val latLng = LatLng(
                        Common.driversFound[offerDriverId]!!.geoLocation!!.latitude,
                        Common.driversFound[offerDriverId]!!.geoLocation!!.longitude)

                    val d = FirebaseDatabase.getInstance().getReference("RiderOffers")
                    var money = ""
                    d.get().addOnSuccessListener { i ->
                        for (ch in i.children){
                            val offer = ch.getValue(DriverOffer::class.java)
                            if (req.riderId == offer?.riderId){
                                money = offer!!.text
                                var session: RideSession = RideSession( offerId = _offerId,
                                    driverId = offerDriverId,
                                    riderId = offerRiderId,
                                    rideState = "Picking_Up",
                                    pickUpLocation = pickUpLocation,
                                    dropOffLocation = dropOffLocation,
                                    driverLat = latLng.latitude,
                                    driverLng = latLng.longitude,
                                    vehicleType = req.vehicleType,
                                    money = money,
                                    midPoint= req.midPoint,
                                    midPointFlag = req.midPointFlag)


                                // deleting all the requests and offers once the offer is accepted
                                val db1 = FirebaseDatabase.getInstance()
                                RiderSession.getCurrentUser { rider ->
                                    //removing existing request for all drivers

                                    val query1 = db1.reference.child("RideRequests")
                                        .orderByChild("riderId")
                                        .equalTo(rider.riderId)

                                    query1.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (docs in dataSnapshot.children) {
                                                docs.ref.removeValue()
                                            }

                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Log.e(
                                                ContentValues.TAG,
                                                "onCancelled",
                                                databaseError.toException()
                                            )
                                        }
                                    })

                                    //removing existing offers for current rider
                                    val query2 = db1.reference.child("RiderOffers")
                                        .orderByChild("riderId")
                                        .equalTo(rider.riderId)

                                    query2.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (docs in dataSnapshot.children) {
                                                docs.ref.removeValue()
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Log.e(
                                                ContentValues.TAG,
                                                "onCancelled",
                                                databaseError.toException()
                                            )
                                        }
                                    })
                                }

                                db = FirebaseDatabase.getInstance().getReference("RideSessions")
                                val offerId = db.push()
                                session.offerId = offerId.key.toString()
                                offerId.setValue(session).addOnSuccessListener {

                                    Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show()
                                    //val intent = Intent(context, RiderRideSessionActivity::class.java)
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        //context.startActivity(intent)
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                                }
                                break
                            }
                        }
                    }

                }
            }
        }

    }
}