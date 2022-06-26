package com.example.swift.frontEnd.rider.riderInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.swift.R
import com.example.swift.businessLayer.session.RiderSession
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_rider_register.*
import kotlinx.android.synthetic.main.fragment_driver_display_information.*
import kotlinx.android.synthetic.main.fragment_rider_display_information.*

class RiderDisplayInformationFragment : Fragment(), AdapterView.OnItemSelectedListener {

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

        // setting button
        riderInfo_updateInfo_btn.setOnClickListener{

            val db  = Firebase.firestore
            val name : String = riderInfo_name_view.text.toString()
            val gender : String = riderInfo_gender_view.text.toString()
            val age : String = riderInfo_age_view.text.toString()
            val email : String = riderInfo_email_view.text.toString()

            val riderToUpdate = mapOf(
                "name" to name,
                "gender" to gender,
                "age" to age,
                "email" to email
            )
            RiderSession.getCurrentUser { rider ->
                db.collection("Rider").document(rider.phoneNumber).update(riderToUpdate)
                    .addOnSuccessListener {
                        db.collection("Driver").document(rider.phoneNumber).update(riderToUpdate)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(),"Information Updated",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                Toast.makeText(requireContext(), " Information Not Updated!!!", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(), " Information Not Updated!!!", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        //setting values in views
        RiderSession.getCurrentUser { rider ->

            riderInfo_name_view.setText(rider.name)

            val spinner: Spinner = riderInfo_gender_spinner
            ArrayAdapter.createFromResource( requireContext(), R.array.gender_list, android.R.layout.simple_spinner_item).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            spinner.onItemSelectedListener = this
            riderInfo_gender_view.setText(rider.gender)

            if(rider.age != "null")
                riderInfo_age_view.setText( rider.age)

            riderInfo_email_view.setText(rider.email)

            riderInfo_rating_view.setText(rider.rating.toString())
        }


    }

    //for gender spinner
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if(position != 0) // stop getting the default first value of spinner
            riderInfo_gender_view.setText( parent?.getItemAtPosition(position).toString())
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}