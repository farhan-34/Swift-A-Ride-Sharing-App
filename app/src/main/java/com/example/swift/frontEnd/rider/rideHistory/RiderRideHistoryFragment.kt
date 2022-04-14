package com.example.swift.frontEnd.rider.rideHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.driver.rideHistory.RideHistoryAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RiderRideHistoryFragment : Fragment() {

    private lateinit var rideHistory : ArrayList<RideSession>
    private  lateinit var rideHistoryRecyclerView: RecyclerView
    private lateinit var adapter: RiderRideHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rider_ride_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        rideHistoryRecyclerView = view.findViewById(R.id.riderHistory_recyclerView)
        loadData()
        initRecycler()
    }

    private fun initRecycler() {
        adapter = RiderRideHistoryAdapter(rideHistory)
        rideHistoryRecyclerView.adapter = adapter
        rideHistoryRecyclerView.layoutManager = LinearLayoutManager(view?.context)
    }

    private fun loadData() {
        rideHistory = ArrayList<RideSession>()
        var db = FirebaseDatabase.getInstance().reference.child("Ride History")

        RiderSession.getCurrentUser { rider ->
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    rideHistory.clear()
                    for(snap in snapshot.children){
                        val session = snap.getValue(RideSession::class.java)
                        if(session?.riderId == rider.riderId){
                            rideHistory.add(session)
                        }
                    }
                    initRecycler()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
}