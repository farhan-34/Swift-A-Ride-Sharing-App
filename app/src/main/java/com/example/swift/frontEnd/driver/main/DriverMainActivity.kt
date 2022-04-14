package com.example.swift.frontEnd.driver.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.RideSession
import com.example.swift.businessLayer.session.DriverSession
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.Services.DriverOnlineService
import com.example.swift.frontEnd.driver.driverInfo.DriverDisplayInformationFragment
import com.example.swift.frontEnd.driver.rideHistory.DriverRideHistoryFragment
import com.example.swift.frontEnd.driver.rideSession.DriverRideSessionActivity
import com.example.swift.frontEnd.driver.riderRequests.DriverRequestListFragment
import com.example.swift.frontEnd.driver.vehicleInfo.DriverVehicleDisplayInformationFragment
import com.example.swift.frontEnd.rider.riderMain.RiderMainActivity
import com.example.swift.frontEnd.rider.signIn.SignInActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_driver_main.*
import kotlinx.android.synthetic.main.driver_menu_header.*

class DriverMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_driver_main)
        supportActionBar?.hide()

        rideSessionStart()


        driver_activate_menu.setOnClickListener {
            driver_drawer.openDrawer(GravityCompat.START)
            driver_activate_menu.hide()
        }

        //setting home fragment
        //supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverHomePageFragment()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverRequestListFragment()).commit()
        driver_nav_view.setCheckedItem(R.id.driver_nav_home)

        //setting the name and rating in the menu header of the driver
        RiderSession.getCurrentUser { rider ->
            driver_menu_name.text = rider.name
        }
        DriverSession.getCurrentUser { driver ->
            driver_menu_rating.text = driver.rating.toString()
        }

        //binding floating button with the slider menu
        driver_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                driver_activate_menu.hide()
                driver_drawer.bringToFront()
            }

            override fun onDrawerOpened(drawerView: View) {
                driver_activate_menu.hide()
                driver_drawer.bringToFront()
            }

            override fun onDrawerClosed(drawerView: View) {
                driver_activate_menu.show()
                driver_main_fragment_container.bringToFront()
            }

            override fun onDrawerStateChanged(newState: Int) {
                //Log.i(TAG, "onDrawerStateChanged");
            }
        })

        //setting buttons
        driver_becomeRider_btn.setOnClickListener {

            val intent = Intent(this, RiderMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

        driver_main_fragment_container.bringToFront()

        driver_nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if(driver_drawer.isDrawerOpen(GravityCompat.START)){
            driver_drawer.closeDrawer(GravityCompat.START)
            driver_activate_menu.show()
        }else {
            super.onBackPressed()
        }
    }
    private fun rideSessionStart() {
        var db = FirebaseDatabase.getInstance().getReference("RideSessions")
        db.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val rideSession: RideSession? = snapshot.getValue(RideSession::class.java)
                val curUser = FirebaseAuth.getInstance().currentUser!!.uid
                if(rideSession != null){
                    if(rideSession.driverId == curUser){
                        val dialogIntent = Intent(this@DriverMainActivity, DriverRideSessionActivity::class.java)
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
            R.id.driver_nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverRequestListFragment()).commit()
            }
            R.id.driver_nav_userInfo -> {
                supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverDisplayInformationFragment()).commit()
            }
            R.id.driver_nav_vehicleInfo -> {
                supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverVehicleDisplayInformationFragment()).commit()
            }
            R.id.driver_nav_RideHistory -> {
                supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverRideHistoryFragment()).commit()
            }

            R.id.driver_nav_logout ->{
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        driver_drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        val intent = Intent(this, DriverOnlineService::class.java)
        this.stopService(intent)

        Toast.makeText(this, "Lagggg gyeeee", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }
}