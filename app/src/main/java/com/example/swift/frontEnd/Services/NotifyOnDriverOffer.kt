package com.example.swift.frontEnd.Services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class NotifyOnDriverOffer() : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Common.endThread = true
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //super.onStartCommand(intent, flags, startId)

//                FirebaseDatabase.getInstance().getReference("RiderOffers")
//                    .addChildEventListener(object : ChildEventListener{
//                        override fun onChildAdded(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?
//                        ) {
//                            val i = snapshot.value.toString()
//                            val temp: DriverOffer? = snapshot.getValue(DriverOffer::class.java)
//                            val curUser = FirebaseAuth.getInstance().currentUser!!.uid
//                            if (temp != null) {
//                                val rider = temp.riderId
//                                if (rider == curUser) {
//                                    if (!Common.offersReceived.contains(temp.offerId)) {
//                                        createNotificationChannel(intent!!)
//                                        Common.offersReceived.add(temp.offerId)
//                                    }
//                                }
//                            }
//                        }
//
//                        override fun onChildChanged(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?
//                        ) {
//
//                        }
//
//                        override fun onChildRemoved(snapshot: DataSnapshot) {
//                        }
//
//                        override fun onChildMoved(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?
//                        ) {
//
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//
//                    })

        return super.onStartCommand(intent, flags, startId)
    }

    public fun createNotificationChannel(intent:Intent) {
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "New Offer"
            val desc = "There is new a new offer for you"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("channel_notify_1", name, importance).apply {
                description = desc
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            var builder = Notification.Builder(this, "channel_notify_1")
                .setSmallIcon(R.drawable.car_logo)
                .setColor(ContextCompat.getColor(this, R.color.main_orange_color))
                .setContentText("New Offer by Driver")
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.car_logo))
                .setContentIntent(pendingIntent)
            notificationManager.notify(123,builder.build())
        }
    }
}