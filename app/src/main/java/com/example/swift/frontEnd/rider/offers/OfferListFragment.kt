package com.example.swift.frontEnd.rider.offers


// romove this fragment later


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.Services.NotifyOnDriverOffer
import com.example.swift.frontEnd.rider.homePage.RiderHomePageFragment
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_offer_list.*
import kotlinx.android.synthetic.main.fragment_rider_offer_list.*

private lateinit var driversOffersList : ArrayList<DriverOffer>
private  lateinit var driversOffersRecyclerView: RecyclerView
private lateinit var adapter: OfferListAdapter
class RiderOfferListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        driversOffersList = ArrayList<DriverOffer>()
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


        val db = FirebaseDatabase.getInstance()
        rider_cancelRide.setOnClickListener {
            RiderSession.getCurrentUser { rider ->
                //removing existing request for all drivers
                val query1 = db.reference.child("RideRequests")
                    .orderByChild("riderId")
                    .equalTo(rider.riderId)

                query1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (docs in dataSnapshot.children) {
                            docs.ref.removeValue()
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException())
                    }
                })

                //removing existing offers for current rider
                val query2 = db.reference.child("RiderOffers")
                    .orderByChild("riderId")
                    .equalTo(rider.riderId)

                query2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (docs in dataSnapshot.children) {
                            docs.ref.removeValue()
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException())
                    }
                })
            }
            //closing service
            Common.endThread = true
            val intent = Intent(requireContext(), NotifyOnDriverOffer::class.java)
            requireContext().stopService(intent)

            //switching to home fragment
            parentFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderHomePageFragment()).addToBackStack(null).commit()
        }
    }


    private fun load_data(){
        var db = FirebaseDatabase.getInstance().getReference().child("RiderOffers")

        RiderSession.getCurrentUser { rider ->
            db.addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val i = snapshot.value.toString()
                        val temp: DriverOffer? = snapshot.getValue(DriverOffer::class.java)

                        if (temp != null) {
                            if (temp.riderId == rider.riderId) {
                                driversOffersList.add(temp!!)
                            }
                        }

                    adapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    var index:Int = 0
                    for(i in 0 until driversOffersList.size)
                    {
                        if(driversOffersList[i].offerId == snapshot.key){
                            index = i
                        }
                    }

                    driversOffersList.removeAt(index)
                    adapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun init_recycler_view(){
        adapter = OfferListAdapter(driversOffersList)
        driversOffersRecyclerView.adapter = adapter
        driversOffersRecyclerView.layoutManager = LinearLayoutManager(view?.context)
    }


}