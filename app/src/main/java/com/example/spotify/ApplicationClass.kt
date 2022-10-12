package com.example.spotify

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/******************************Applicaton class is the base class as soon the apllication is install this class runs******/
/*****************************************It will help us to make the notification***************************************/
class ApplicationClass: Application() {

    companion object {
        val CHANNEL_ID = "channel1"
        val PLAY = "play"
        val NEXT = "next"
        val PREVIOUS = "previous"
        val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            //Create an Notification Channel
            val notificationChannel=NotificationChannel(CHANNEL_ID,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description="This is a important channel for showing songs"

            //for creating channel at runtime
            val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}