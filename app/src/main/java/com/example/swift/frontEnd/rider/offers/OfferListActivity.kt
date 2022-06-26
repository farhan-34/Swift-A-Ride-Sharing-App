package com.example.swift.frontEnd.rider.offers

import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.Services.NotifyOnDriverOffer
import com.example.swift.frontEnd.rider.riderMain.RiderMainActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_offer_list.*

class OfferListActivity : AppCompatActivity() {

    private lateinit var driversOffersList : ArrayList<DriverOffer>
    private  lateinit var driversOffersRecyclerView: RecyclerView
    private lateinit var adapter: OfferListAdapter

    // fun to inflate the popup of sort by
    private fun showPopupSortBy(view: View) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.offer_sortby_items)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.title) {
                "Fair" -> {
                    // Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()

                    driversOffersList?.sortBy { it.text.toInt() }
                    init_recycler_view()
                }
                "Distance" -> {
                    // Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()

                    driversOffersList?.sortBy { it.rating }
                    init_recycler_view()
                }
                "Rating" -> {
                    //Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()

                    driversOffersList?.sortByDescending { it.rating }
                    init_recycler_view()
                }
            }

            true
        })

        popup.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_list)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        supportActionBar?.hide()

        driversOffersList = ArrayList<DriverOffer>()

        driversOffersRecyclerView = findViewById(R.id.driversOffers_recycler)
        load_data()
        init_recycler_view()

        rider_driversOffers_sort.setOnClickListener{


            showPopupSortBy(rider_driversOffers_sort)


            Toast.makeText(this, "Sort applied", Toast.LENGTH_SHORT).show()
        }


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
                        Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
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
                        Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                    }
                })
            }
            //closing service
            Common.endThread = true
            val intent = Intent(this, NotifyOnDriverOffer::class.java)
            this.stopService(intent)

            //switching to home fragment
            startActivity(Intent(this, RiderMainActivity::class.java))
            this.finish()
        }
    }

    private fun load_data(){
        var db = FirebaseDatabase.getInstance().getReference().child("RiderOffers")

        RiderSession.getCurrentUser { rider ->
            db.addChildEventListener(object: ChildEventListener {
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

                    if(driversOffersList.size >0) {
                        driversOffersList.removeAt(index)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun init_recycler_view(){
        adapter = OfferListAdapter( driversOffersList)
        driversOffersRecyclerView.adapter = adapter
        driversOffersRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}