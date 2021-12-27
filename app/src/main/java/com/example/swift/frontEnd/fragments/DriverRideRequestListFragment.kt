package com.example.swift.frontEnd.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.frontEnd.adapters.RideRequestListAdapter
import androidx.recyclerview.widget.LinearLayoutManager


import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_driver_ride_request_list.*


private lateinit var rideRequestList : ArrayList<RideRequest>
private  lateinit var driverRiderRequestRecyclerView: RecyclerView
class DriverRequestListFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_ride_request_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rideRequest_sort_btn.setOnClickListener{
            Toast.makeText(view.context, "Sort applied", Toast.LENGTH_SHORT).show()
        }

        driverRiderRequestRecyclerView = view.findViewById(R.id.Driver_RiderRequest_RecyclerView)
        loadData()
        init_recycler_view()
    }

    private fun loadData(){
        rideRequestList = ArrayList<RideRequest>()

        var db = FirebaseDatabase.getInstance().reference.child("RideRequests")

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rideRequestList.clear()
                for (snap in snapshot.children) {
                    val i = snap.value.toString()
                    val temp: RideRequest? = snap.getValue(RideRequest::class.java)
                    if (temp != null) {
                        rideRequestList.add(temp)
                    }
                }
                // driverRiderRequestRecyclerView.adapter?.notifyDataSetChanged()
                init_recycler_view()

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun init_recycler_view(){
        var adapter = RideRequestListAdapter(rideRequestList)
        driverRiderRequestRecyclerView.adapter = adapter
        driverRiderRequestRecyclerView.layoutManager = LinearLayoutManager(view?.context)
    }
}