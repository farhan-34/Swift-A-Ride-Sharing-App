package com.example.swift.frontEnd.driver.vehicleInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.swift.R
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.businessLayer.session.RiderSession
import kotlinx.android.synthetic.main.fragment_driver_display_information.*
import kotlinx.android.synthetic.main.fragment_driver_vehicle_display_information.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DriverVehicleDisplayInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DriverVehicleDisplayInformationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_driver_vehicle_display_information,
            container,
            false
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DriverVehicleDisplayInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DriverVehicleDisplayInformationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting values in views
        DriverSession.getCurrentUser { driver ->
            RiderSession.getCurrentUser { rider ->
                vehicleInfo_owner_name_view.text = rider.name
                vehicleInfo_vehicleType_view.text = driver.vehicleType
                vehicleInfo_licenseNumber_view.text = driver.licenseNumber
                vehicleInfo_capacity_view.text = driver.vehicleCapacity
            }
        }
    }
}