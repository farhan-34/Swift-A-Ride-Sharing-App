package com.example.swift.frontEnd.rider.chat

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import com.example.swift.businessLayer.businessLogic.RideRequest
import com.example.swift.businessLayer.dataClasses.ChatMessage
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.example.swift.businessLayer.session.RiderSession
import com.example.swift.frontEnd.driver.riderRequests.RideRequestListAdapter
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

    private var offer: DriverOffer? = null
    private var request: RideRequest? = null
    private var toId : String? = null
    private var toName : String? = null
    private var isOffer = false
    private var isRequest = false
    private var offerID : String? = null
    private var isRider : Boolean = false



    companion object {
        val TAG = "ChatLog"
    }


    val adapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //fetching values from the intent
        offer = intent.getParcelableExtra(OfferListAdapter.OFFER_KEY)
        request = intent.getParcelableExtra(RideRequestListAdapter.REQUEST_KEY)
        isRider = intent.extras!!.getBoolean(OfferListAdapter.IS_RIDER)

        if(offer != null){
            offerID = offer?.offerId
            toId = offer?.driverId
            toName = offer?.driverName
            isOffer = true
        }
        if(request != null){
            toId = request?.riderId
            toName = request?.riderName
            isRequest = true
        }
        if(isRider)
            update_offer_btn_chat_log.visibility = View.INVISIBLE

        // setting some variables
        supportActionBar?.title = toName
        recyclerview_chat_log.adapter = adapter


        // calling the action bar
        var actionBar = supportActionBar
        // showing the back button in action bar
        actionBar?.setDisplayHomeAsUpEnabled(true)


        // showing previous messages
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            val text = edittext_chat_log.text.toString()
            performSendMessage(text,false)
        }

        update_offer_btn_chat_log.setOnClickListener{
            Log.d(TAG, "Attempt to update offer....")

            val window = PopupWindow(this)
            val view = LayoutInflater.from(this).inflate(R.layout.popup_send_offer_layout, null)
            window.contentView = view
            window.isFocusable = true
            window.showAtLocation(view, Gravity.CENTER, 0 , 0);
            window.isOutsideTouchable = false;
            window.update()
            val sndOfferBtn = view.findViewById<Button>(R.id.popup_send_offer_btn)
            val cancelOffer = view.findViewById<Button>(R.id.popup_cancleOffer_btn)
            sndOfferBtn.setOnClickListener{
                //check validation
                val temp: EditText = view.findViewById(R.id.popup_offer_price_view)
                val fair = temp.text.toString()
                if(isPositiveNumber(fair)) {
                    //val db = FirebaseDatabase.getInstance().getReference("/RideOffers/$offerID")
                    performSendMessage(fair, true)

                    window.dismiss()


                }else {
                    Toast.makeText(view.context, "Price not valid", Toast.LENGTH_SHORT).show()
                }
            }
            cancelOffer.setOnClickListener{
                window.dismiss()
            }
            window.showAsDropDown(update_offer_btn_chat_log)
        }



    }



    private fun listenForMessages() {
        RiderSession.getCurrentUser { rider ->
            val fromId = rider.riderId

            val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

            ref.addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val chatMessage = p0.getValue(ChatMessage::class.java)

                    if (chatMessage != null) {
                        Log.d(TAG, chatMessage.text)

                        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                            //val currentUser = LatestMessagesActivity.currentUser ?: return
                            //adapter.add(ChatFromItem(chatMessage.text, currentUser))
                            if(chatMessage.offer)
                                adapter.add(OfferMsgFromItem(chatMessage.text))
                            else
                                adapter.add(ChatFromItem(chatMessage.text))

                        }

                        else {
                            //adapter.add(ChatToItem(chatMessage.text, toUser!!))

                            if(chatMessage.offer)
                                adapter.add(OfferMsgToItem(chatMessage.text))
                            else
                                adapter.add(ChatToItem(chatMessage.text))
                        }
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
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

    private fun performSendMessage(text:String, isOffer:Boolean) {

        RiderSession.getCurrentUser { rider ->

            val fromId = rider.riderId



            if (text.isEmpty())
                return@getCurrentUser


            //    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
            val reference =
                FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

            val toReference =
                FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

            val chatMessage = toId?.let {
                ChatMessage(
                    reference.key!!, text, fromId!!,
                    it, System.currentTimeMillis() / 1000, isOffer
                )
            }

            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }

            toReference.setValue(chatMessage)
        }
    }


    // updating home up button
    // going back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun isPositiveNumber(string: String): Boolean {

        var numeric = true

        numeric = string.matches("0?\\d+(\\.\\d+)?".toRegex())
        return numeric
    }



}