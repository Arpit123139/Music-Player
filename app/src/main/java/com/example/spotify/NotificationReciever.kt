package com.example.spotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReciever :BroadcastReceiver() {

    private lateinit var playerActivity:PlayerActivity
    private lateinit var musicService:MusicService

    override fun onReceive(context: Context?, intent: Intent?) {

        playerActivity= PlayerActivity();
        musicService= MusicService()
        /***********************************TO RECIEVE THE ACTION FROM THE MUSIC SERVICE FOR NOTIFICATION****************/
        when(intent?.action){

            ApplicationClass.PREVIOUS-> prevNextSong(false,context!!)
           ApplicationClass.PLAY-> { if(PlayerActivity.isPlaying==true) pauseMusic()
                else playMusic()
            }
            ApplicationClass.NEXT-> prevNextSong(true,context!!)
            ApplicationClass.EXIT-> {
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService=null;
                PlayerActivity.musicService!!.mediaPlayer!!.release()
                exitProcess(1)                   // Status code is 1 because we are willing closing the apllication
            }


        }

    }

    private fun playMusic(){
        PlayerActivity.isPlaying=true;
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotificaton(R.drawable.pause_icon,1f)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying=false;
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotificaton(R.drawable.play_icon,0f)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
    }

    private fun prevNextSong(increment:Boolean,context: Context){

        setSongPosition(increment=increment)
        // Now after the next song we want the media player to create again so next song is played
         PlayerActivity.musicService!!.createMediaPlayer()

        //set the button to pause inside the notification
        PlayerActivity.musicService!!.showNotificaton(R.drawable.pause_icon,1f)

        // when we click the notification it should also set the layout of the mainScreen with the image and the song Name so we use the setLayout Method in PlayerActivity

        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(
            RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
            .into(PlayerActivity.binding.songImgPA)
        PlayerActivity.binding.songNamePA.text= PlayerActivity.musicListPA[PlayerActivity.songPosition].title

        // Changing the layout of NowFragment as the next button in the notification is clicked
        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(
            RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
            .into(NowPlaying.binding.songImgNP)

        NowPlaying.binding.SongNameNP.text =PlayerActivity.musicListPA[PlayerActivity.songPosition].title;
        playMusic()
        /**********************************This function is responsible when we change the song through the notification the favourite icon shoulb be set Accordingly**/
        PlayerActivity.fIndex= favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)            // This function changes the isFavourite value and also return the index
        if(PlayerActivity.isFavourite) PlayerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else PlayerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)

    }
}