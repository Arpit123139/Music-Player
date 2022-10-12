package com.example.spotify

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicService: Service() {

    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
// If we are running more than one app which plays the audio then each notification has a id which is a mediaSession

        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder: Binder(){                                    // Binder is use to attach the Activity with the service
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotificaton(playPauseBtn:Int){
        /******************************Pending Intent -- A PendingIntent object wraps the functionality of an Intent object while allowing your app to specify something that another app should do, on your app’s behalf, in response to a future action*/

        val notificationIntent = Intent(this, MainActivity::class.java)              // when the notification is click go to the MainActivity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val prevIntent=Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.PREVIOUS);        // We dont want to shift between the class we only want the action to go there
        val prevPendingIntent=PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)     // By passing the flag means that we can update the intent in the NotificationRecievr

        val nextIntent=Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.NEXT);
        val nextPendingIntent=PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent=Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.PLAY);
        val playPendingIntent=PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent=Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.EXIT);
        val exitPendingIntent=PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        /*********************************************Decoding The BitMap Array*************************************/
        val imgArt= getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if(imgArt!=null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.music_player_icon_slash_screen)
        }

        val notification= androidx.core.app.NotificationCompat.Builder(this,ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)                             // Title of the notification
                    .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
                    .setSmallIcon(R.drawable.music_icon)
                    .setLargeIcon(image)
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))           //
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)                         // for every song it should not have different notification the notification should be same
                    .addAction(R.drawable.previous_icon,"Previous",prevPendingIntent)           // The sequence is very imp
                    .addAction(playPauseBtn,"Play",playPendingIntent)
                    .addAction(R.drawable.next_icon,"Next",nextPendingIntent)
                    .addAction(R.drawable.exit_icon,"Exit",exitPendingIntent)
                    .setContentIntent(pendingIntent)
                    .build()

        startForeground(12,notification)

    }

    //first time when we start the Service then first the OnCreate() is called which calls the onStartCommand if again the activity is called then directly onStartCommand() is called
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    /*****************************Play The Audio***************************************************************************************/
    fun createMediaPlayer(){
        try {
            if(PlayerActivity.musicService!!.mediaPlayer==null)
                PlayerActivity.musicService!!.mediaPlayer= MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        }catch (e:Exception){
            return
        }
    }

}