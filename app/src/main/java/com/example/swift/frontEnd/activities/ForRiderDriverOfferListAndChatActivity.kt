package com.example.swift.frontEnd.activities

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.swift.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.rider_menu_slider.*


class ForRiderDriverOfferListAndChatActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_for_rider_driver_offer_list_and_chat)

        activate_menu.setOnClickListener {
            rider_drawer.openDrawer(Gravity.LEFT)
            activate_menu.hide()
        }

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
        return true
    }
}