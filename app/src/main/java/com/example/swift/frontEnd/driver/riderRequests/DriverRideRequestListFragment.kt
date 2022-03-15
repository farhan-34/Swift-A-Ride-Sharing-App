package com.example.swift.frontEnd.driver.riderRequests

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.frontEnd.driver.riderRequests.RideRequestListAdapter
import androidx.recyclerview.widget.LinearLayoutManager


import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.frontEnd.Services.DriverOnlineService
import com.example.swift.frontEnd.Services.NotifyOnDriverOffer
import com.example.swift.frontEnd.activities.RequestDriverActivity
import com.example.swift.frontEnd.driver.rideSession.DriverRideSessionActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_driver_ride_request_list.*


private lateinit var rideRequestList : ArrayList<RideRequest>
private  lateinit var driverRiderRequestRecyclerView: RecyclerView
private lateinit var adapter: RideRequestListAdapter
class DriverRequestListFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(requireContext(), DriverOnlineService::class.java)
        requireContext().startService(intent)
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

        rideSessionStart()

        val db = FirebaseDatabase.getInstance()


        driverRiderRequestRecyclerView = view.findViewById(R.id.Driver_RiderRequest_RecyclerView)
        loadData()
        init_recycler_view()
    }

    private fun loadData(){
        rideRequestList = ArrayList<RideRequest>()

        var db = FirebaseDatabase.getInstance().reference.child("RideRequests")
        DriverSession.getCurrentUser { driver ->
            db.addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val i = snapshot.value.toString()
                    val temp: RideRequest? = snapshot.getValue(RideRequest::class.java)

                    if (temp != null) {
                        if (temp.driverId == driver.driverId) {
                            rideRequestList.add(temp!!)
                        }
                    }

                    adapter.notifyDataSetChanged()
                    //adapter.notifyItemInserted(rideRequestList.size)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    var index:Int = 0
                    for(i in 0 until rideRequestList.size)
                    {
                        if(rideRequestList[i].requestId == snapshot.key){
                            index = i
                        }
                    }

                    rideRequestList.removeAt(index)
                    adapter.notifyItemRemoved(index)

                //adapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

//        db.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                rideRequestList.clear()
//                for (snap in snapshot.children) {
//                    val i = snap.value.toString()
//                    val temp: RideRequest? = snap.getValue(RideRequest::class.java)
//                    if (temp != null) {
//                        rideRequestList.add(temp)
//                    }
//                }
//                // driverRiderRequestRecyclerView.adapter?.notifyDataSetChanged()
//                init_recycler_view()
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
    }

    private fun init_recycler_view(){
        adapter = RideRequestListAdapter(rideRequestList)
        driverRiderRequestRecyclerView.adapter = adapter
        driverRiderRequestRecyclerView.layoutManager = LinearLayoutManager(view?.context)
    }

    private fun rideSessionStart() {
        var db = FirebaseDatabase.getInstance().getReference("RideSessions")
        db.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val rideSession: RideSession? = snapshot.getValue(RideSession::class.java)
                val curUser = FirebaseAuth.getInstance().currentUser!!.uid
                if(rideSession != null){
                    if(rideSession.driverId == curUser){
                        Common.rideSession = rideSession


                        val dialogIntent = Intent(requireContext(), DriverRideSessionActivity::class.java)
                        //val dialogIntent = Intent(requireContext(), RequestDriverActivity::class.java)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(dialogIntent)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}