package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.swift.R
import com.example.swift.frontEnd.fragments.RiderHomePageFragment
import com.example.swift.frontEnd.fragments.RiderRequestListFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_rider_main.*


class RiderMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_rider_main)
        supportActionBar?.hide()

        activate_menu.setOnClickListener {
            rider_drawer.openDrawer(GravityCompat.START)
            activate_menu.hide()
        }

        //setting default fragment
        supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderHomePageFragment()).commit()
        rider_nav_view.setCheckedItem(R.id.nav_home)

        rider_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                activate_menu.hide()
            }

            override fun onDrawerOpened(drawerView: View) {
                activate_menu.hide()
            }

            override fun onDrawerClosed(drawerView: View) {
                activate_menu.show()
            }

            override fun onDrawerStateChanged(newState: Int) {
                //Log.i(TAG, "onDrawerStateChanged");
            }
        })


        rider_becomeDriver_btn.setOnClickListener {
            startActivity(Intent(this, DriverRegistrationActivity::class.java))
        }

        rider_nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if(rider_drawer.isDrawerOpen(GravityCompat.START)){
            rider_drawer.closeDrawer(GravityCompat.START)
            activate_menu.show()
        }else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderHomePageFragment()).commit()
            }
            R.id.nav_riderRideHistory -> {
                supportFragmentManager.beginTransaction().replace(R.id.rider_main_fragment_container, RiderRequestListFragment()).commit()
            }
        }

        rider_drawer.closeDrawer(GravityCompat.START)
        return true
    }
}