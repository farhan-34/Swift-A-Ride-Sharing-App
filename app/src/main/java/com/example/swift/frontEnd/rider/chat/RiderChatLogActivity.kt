package com.example.swift.frontEnd.rider.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.swift.R
import com.example.swift.businessLayer.dataClasses.ChatMessage
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.rider.offers.OfferListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class RiderChatLogActivity : AppCompatActivity() {

    var offer: DriverOffer? = null
    var fromId : String? = null
    var toId : String? = null

    val adapter = GroupAdapter<GroupieViewHolder>()


    companion object {
        val TAG = "ChatLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        offer = intent.getParcelableExtra<DriverOffer>(OfferListAdapter.OFFER_KEY)
        supportActionBar?.title = offer?.name


        recyclerview_chat_log.adapter = adapter

        toId = offer?.driverId
        RiderSession.getCurrentUser { rider ->
            fromId = rider.riderId
        }


        // calling the action bar
        var actionBar = supportActionBar

        // showing the back button in action bar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //setupDummyData()

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }

    }

//    private fun setupDummyData(){
//        val adapter = GroupAdapter<GroupieViewHolder>()
//
//        adapter.add(ChatFromItem(""))
//        adapter.add(ChatToItem(""))
//        adapter.add(ChatFromItem(""))
//        adapter.add(ChatToItem(""))
//        adapter.add(ChatFromItem(""))
//        adapter.add(ChatToItem(""))
//
//        recyclerview_chat_log.adapter = adapter
//    }





    private fun listenForMessages() {
        RiderSession.getCurrentUser { rider ->
            fromId = rider.riderId

            val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

            ref.addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val chatMessage = p0.getValue(ChatMessage::class.java)

                    if (chatMessage != null) {
                        Log.d(TAG, chatMessage.text)

                        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                            //val currentUser = LatestMessagesActivity.currentUser ?: return
                            //adapter.add(ChatFromItem(chatMessage.text, currentUser))
                            adapter.add(ChatFromItem(chatMessage.text))
                        } else {
                            //adapter.add(ChatToItem(chatMessage.text, toUser!!))
                            adapter.add(ChatToItem(chatMessage.text))
                        }
                    }

                }


                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }


            })
        }

    }

    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()

        //val fromId = FirebaseAuth.getInstance().uid
        //val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        //val toId = user.uid

        if (fromId == null) return

//    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = toId?.let {
            ChatMessage(reference.key!!, text, fromId!!,
                it, System.currentTimeMillis() / 1000)
        }

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)
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
//
//class ChatFromItem: Item<GroupieViewHolder>(){
//    override fun bind(viewHolder: GroupieViewHolder, position: Int){
//
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.chat_from_row
//    }
//}
//
//class ChatToItem: Item<GroupieViewHolder>(){
//    override fun bind(viewHolder: GroupieViewHolder, position: Int){
//
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.chat_to_row
//    }
//}