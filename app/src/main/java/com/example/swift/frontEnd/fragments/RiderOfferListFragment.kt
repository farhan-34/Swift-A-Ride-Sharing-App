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
import com.example.swift.frontEnd.adapters.DriverOfferListAdapter

private lateinit var driversOffersList : ArrayList<DriverOffer>
private  lateinit var recyclerView: RecyclerView
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

        recyclerView =  view.findViewById(R.id.driversOffers_recycler)
        load_data()
        init_recycler_view()
    }


    private fun load_data(){
        driversOffersList = ArrayList<DriverOffer>()
        for (i in 1..10) {
            var obj = DriverOffer("1","Farhan","I will take you there for 100Rs.", "url",
                4.8)

            driversOffersList.add(obj)
        }

    }

    private fun init_recycler_view(){
        var adapter = DriverOfferListAdapter(driversOffersList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view?.context)
    }


}