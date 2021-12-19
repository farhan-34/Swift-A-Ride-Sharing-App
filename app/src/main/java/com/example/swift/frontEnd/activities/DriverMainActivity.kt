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
import com.example.swift.frontEnd.fragments.DriverHomePageFragment
import com.example.swift.frontEnd.fragments.DriverRequestListFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_driver_main.*
import kotlinx.android.synthetic.main.activity_rider_main.*

class DriverMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_driver_main)
        supportActionBar?.hide()

        driver_activate_menu.setOnClickListener {
            driver_drawer.openDrawer(GravityCompat.START)
            driver_activate_menu.hide()
        }

        //setting home fragment
        supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverHomePageFragment()).commit()
        driver_nav_view.setCheckedItem(R.id.driver_nav_home)


        //binding floating button with the slider menu
        driver_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                driver_activate_menu.hide()
            }

            override fun onDrawerOpened(drawerView: View) {
                driver_activate_menu.hide()
            }

            override fun onDrawerClosed(drawerView: View) {
                driver_activate_menu.show()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.driver_nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverHomePageFragment()).commit()
            }
            R.id.driver_nav_requests -> {
                supportFragmentManager.beginTransaction().replace(R.id.driver_main_fragment_container, DriverRequestListFragment()).commit()
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
}