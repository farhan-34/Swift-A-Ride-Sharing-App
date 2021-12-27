package com.example.swift.frontEnd.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.adapters.DriverOfferListAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var driversOffersList : ArrayList<DriverOffer>
private  lateinit var driversOffersRecyclerView: RecyclerView
class RiderOfferListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rider_offer_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        driversOffersRecyclerView =  view.findViewById(R.id.driversOffers_recycler)
        load_data()
        init_recycler_view()
    }


    private fun load_data(){
        driversOffersList = ArrayList<DriverOffer>()
        var db = FirebaseDatabase.getInstance().getReference().child("RiderOffers")

        RiderSession.getCurrentUser { rider ->
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    driversOffersList.clear()
                    for (snap in snapshot.children) {
                        val i = snap.value.toString()
                        val temp: DriverOffer? = snap.getValue(DriverOffer::class.java)

                        if (temp != null) {
                            if (temp.riderId == rider.riderId) {
                                driversOffersList.add(temp!!)
                            }
                        }
                        driversOffersRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun init_recycler_view(){
        var adapter = DriverOfferListAdapter(requireContext(), driversOffersList)
        driversOffersRecyclerView.adapter = adapter
        driversOffersRecyclerView.layoutManager = LinearLayoutManager(view?.context)
    }


}