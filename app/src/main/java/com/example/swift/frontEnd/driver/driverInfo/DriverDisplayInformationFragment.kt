package com.example.swift.frontEnd.driver.driverInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.swift.R
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.businessLayer.session.RiderSession
import kotlinx.android.synthetic.main.fragment_driver_display_information.*
import kotlinx.android.synthetic.main.fragment_rider_display_information.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DriverDisplayInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DriverDisplayInformationFragment : Fragment(), AdapterView.OnItemSelectedListener {
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
        return inflater.inflate(R.layout.fragment_driver_display_information, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DriverDisplayInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DriverDisplayInformationFragment().apply {
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
                driverInfo_name_view.setText(rider.name)
                if(rider.age != "null")
                    driverInfo_age_view.setText(rider.age + " years")

                driverInfo_email_view.setText(driver.email)

                val spinner: Spinner = driverInfo_gender_spinner
                ArrayAdapter.createFromResource( requireContext(), R.array.gender_list, android.R.layout.simple_spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
                spinner.onItemSelectedListener = this
                driverInfo_gender_view.setText(rider.gender)

                driverInfo_CNIC_view.setText( driver.cnic)
                driverInfo_rating_view.setText( driver.rating.toString())
            }
        }
    }

    //for gender spinner
    private var spinnerCount = 0
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // to stop getting the default first value of spinner
        val array: Array<String> = resources.getStringArray(R.array.gender_list)
        if (driverInfo_gender_view.text.toString() == array[0] && spinnerCount == 0)
        {
            spinnerCount++
        }
        else
        {
            spinnerCount++
            driverInfo_gender_view.setText( parent?.getItemAtPosition(position).toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}