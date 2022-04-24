package com.example.swift.frontEnd.rider.riderMain

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.swift.R
import com.example.swift.businessLayer.BroadCasts.InternetConnectivityBroadcastReceiver
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.last_login_stats.LastLoginStats
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.driver.main.DriverMainActivity
import com.example.swift.frontEnd.driver.registration.DriverRegistrationActivity
import com.example.swift.frontEnd.rider.donation.DonationFragment
import com.example.swift.frontEnd.rider.riderInfo.RiderDisplayInformationFragment
import com.example.swift.frontEnd.rider.homePage.RiderHomePageFragment
import com.example.swift.frontEnd.rider.offers.RiderOfferListFragment
import com.example.swift.frontEnd.rider.panicButton.PanicButtonActivity
import com.example.swift.frontEnd.rider.panicButton.PanicButtonFragment
import com.example.swift.frontEnd.rider.rideHistory.RiderRideHistoryFragment
import com.example.swift.frontEnd.rider.rideSession.RiderRideSessionActivity
import com.example.swift.frontEnd.rider.signIn.SignInActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_rider_main.*
import kotlinx.android.synthetic.main.rider_menu_header.*


class RiderMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private val connectivityInternet: BroadcastReceiver = InternetConnectivityBroadcastReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_rider_main)
        supportActionBar?.hide()

        checkSession()

        // switch To Driver If Last Login was as driver
        RiderSession.getCurrentUser { rider ->
            if(rider.isLastTimeDriverLogin == "true"){
                val intent = Intent(this, DriverMainActivity::class.java)
                startActivity(intent)
            }
        }

        activate_menu.setOnClickListener {
            rider_drawer.openDrawer(GravityCompat.START)
            activate_menu.hide()
        }
        RiderSession.getCurrentUser { rider ->
        rider_menu_riderName.text = rider.name
            rider_menu_rating.text = rider.rating.toString()
        }

        //setting default fragment
        supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderHomePageFragment()).addToBackStack(null).commit()
        rider_nav_view.setCheckedItem(R.id.nav_home)

        rider_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                activate_menu.hide()
                rider_drawer.bringToFront()
            }

            override fun onDrawerOpened(drawerView: View) {
                activate_menu.hide()
                rider_drawer.bringToFront()
            }

            override fun onDrawerClosed(drawerView: View) {
                activate_menu.show()
                rider_main_fragment_container.bringToFront()
            }

            override fun onDrawerStateChanged(newState: Int) {
                //Log.i(TAG, "onDrawerStateChanged");
            }
        })


        // Become Driver Button
        rider_becomeDriver_btn.setOnClickListener {
            //checking if the rider already registered as a driver or not
            RiderSession.getCurrentUser { rider ->
                if(rider.isdriver == "true") {
                    val intent = Intent(this, DriverMainActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    //finish()

                }
                else {
                    startActivity(Intent(this, DriverRegistrationActivity::class.java))
                }
            }
        }

        rider_nav_view.setNavigationItemSelectedListener(this)
    }

    //for broadcasting
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        registerReceiver(connectivityInternet, filter)
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(connectivityInternet)
    }

    override fun onBackPressed() {
        if(rider_drawer.isDrawerOpen(GravityCompat.START)){
            rider_drawer.closeDrawer(GravityCompat.START)
            activate_menu.show()
        }else {
            super.onBackPressed()
        }
    }

    private fun checkSession() {
        var db = FirebaseDatabase.getInstance().getReference("RideSessions")
        db.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val rideSession: RideSession? = snapshot.getValue(RideSession::class.java)
                val curUser = FirebaseAuth.getInstance().currentUser!!.uid
                if(rideSession != null){
                    if(rideSession.riderId == curUser){
                        val dialogIntent = Intent(this@RiderMainActivity, RiderRideSessionActivity::class.java)
                        //val dialogIntent = Intent(requireContext(), RequestDriverActivity::class.java)
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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderHomePageFragment()).addToBackStack(null).commit()
            }
            R.id.nav_userInfo ->{
                supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderDisplayInformationFragment()).addToBackStack(null).commit()
            }
            R.id.nav_riderPanic ->{
                supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, PanicButtonFragment()).addToBackStack(null).commit()
            }
            R.id.nav_riderRideHistory->{
                supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderRideHistoryFragment()).addToBackStack(null).commit()
            }
            R.id.nav_riderDonationPage->{
                supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, DonationFragment()).addToBackStack(null).commit()
            }

            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }

        rider_drawer.closeDrawer(GravityCompat.START)
        return true
    }

}