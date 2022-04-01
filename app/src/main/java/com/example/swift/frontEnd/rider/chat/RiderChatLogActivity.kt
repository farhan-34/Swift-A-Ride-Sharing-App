package com.example.swift.frontEnd.rider.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.frontEnd.rider.offers.OfferListAdapter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.text.FieldPosition


class RiderChatLogActivity : AppCompatActivity() {

    var offer: DriverOffer? = null


    companion object {
        val TAG = "ChatLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        offer = intent.getParcelableExtra<DriverOffer>(OfferListAdapter.OFFER_KEY)
        supportActionBar?.title = offer?.name




        // calling the action bar
        var actionBar = supportActionBar

        // showing the back button in action bar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setupDummyData()

    }

    private fun setupDummyData(){
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())

        recyclerview_chat_log.adapter = adapter
    }

    // TODO homeUp button not working
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }


}

class ChatFromItem: Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem: Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}