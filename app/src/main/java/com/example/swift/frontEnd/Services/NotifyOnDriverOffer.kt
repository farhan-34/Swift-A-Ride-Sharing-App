package com.example.swift.frontEnd.Services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.swift.R
import com.example.swift.businessLayer.Common.Common
import com.example.swift.businessLayer.dataClasses.DriverOffer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotifyOnDriverOffer : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Common.endThread = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //super.onStartCommand(intent, flags, startId)
        Thread(object :Runnable{
            override fun run() {
                while (!Common.endThread) {
                    Log.d("Service thread", "Service Thread")
                    FirebaseDatabase.getInstance().getReference("RiderOffers")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (snap in snapshot.children) {
                                    val i = snap.value.toString()
                                    val temp: DriverOffer? = snap.getValue(DriverOffer::class.java)
                                    val curUser = FirebaseAuth.getInstance().currentUser!!.uid
                                    if (temp != null) {
                                        val rider = temp.riderId
                                        if (rider == curUser) {
                                            if(!Common.offersReceived.contains(temp.offerId)) {
                                                createNotificationChannel(intent!!)
                                                Common.offersReceived.add(temp.offerId)
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }
            }
        }).start()
        try{
            Thread.sleep(2000)

        }catch (e: InterruptedException){
            e.printStackTrace()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel(intent:Intent) {
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