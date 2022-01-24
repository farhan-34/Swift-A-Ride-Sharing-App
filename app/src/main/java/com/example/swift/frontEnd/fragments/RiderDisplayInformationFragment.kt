package com.example.swift.frontEnd.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.swift.R
import com.example.swift.businessLayer.session.RiderSession
import kotlinx.android.synthetic.main.fragment_rider_display_information.*

class RiderDisplayInformationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rider_display_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting values in views
        RiderSession.getCurrentUser { rider ->
            riderInfo_name_view.text = rider.name
            riderInfo_gender_view.text = rider.gender
            if(rider.age == "null")
                riderInfo_age_view.text = "Not submitted yet"
            else
                riderInfo_age_view.text = rider.age + " years"
            vehicleInfo_licenseNumber_view.text = rider.email
        }


    }
}