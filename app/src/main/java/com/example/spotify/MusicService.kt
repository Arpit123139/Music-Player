package com.example.spotify

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat

class MusicService: Service(),AudioManager.OnAudioFocusChangeListener {              /******This Library is added so that if the call arrive than the song must stop and call should continue*******/

    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable                        /******************Helps us to execute the same code again and again**/
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
    fun showNotificaton(playPauseBtn:Int,plabackSpeed:Float){
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

        /****************************************************SEEKBAR IN NOTIFICATION**************************************/
        //This feature is Available in Android10 and above
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,mediaPlayer!!.duration.toLong())
                .build())

            mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer!!.currentPosition.toLong(),plabackSpeed)    // Jab song playing ho toh uski position kya honi chahoiye  AUR PLAYBACK BATATA HAI KI SEEKBAR KI SPEED KYA HONI CHAHIYE JAB PAUSE HO TOH 0f AUR JAB PLAY TOH 1f ki 1 sec se bade
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build())

        }
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

            /**************************************Setting the current time stamp of the song and the final Duration and the seekbar WHEN WE CLICK NEXT IN THE NOTIFICATION *****/
            PlayerActivity.binding.tvSeekBarStart.text= formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text= formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress= 0
            PlayerActivity.binding.seekBarPA.max= PlayerActivity.musicService!!.mediaPlayer!!.duration

            /*****************this code is added to make sure when the current song is playing and we click on the same song again thaen it does not begin from start*********/
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id

        }catch (e:Exception){
            return
        }
    }

    fun seekBarSetup(){
        runnable= Runnable {
            PlayerActivity.binding.tvSeekBarStart.text= formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress= mediaPlayer!!.currentPosition

            /***********IT tells after how much time the code should run**********************************************/
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        //It makes assure  the inner/above code should execute after 0ms after this code is executed then we went to the inner Handler which say the code inside the runnable should execute after 200ms
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)

    }

    /******This Library is added so that if the call arrive than the song must stop and call should continue*******/
    override fun onAudioFocusChange(focusChange: Int) {

        if(focusChange<=0){                // request is unsuccesfull
            //pause Music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            PlayerActivity.musicService!!.showNotificaton(R.drawable.play_icon,0f)
            //fOR cHANGING THE ICON OF NowPlaing
           NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
            PlayerActivity.isPlaying =false
            PlayerActivity.musicService!!.mediaPlayer!!.pause()
        }else{
            //play Music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotificaton(R.drawable.pause_icon,1f)
            //fOR cHANGING THE ICON OF NowPlaing
            NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            PlayerActivity.isPlaying =true
            PlayerActivity.musicService!!.mediaPlayer!!.start()
        }
    }

}