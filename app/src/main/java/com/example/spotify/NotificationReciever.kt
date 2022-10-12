package com.example.spotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlin.system.exitProcess

class NotificationReciever :BroadcastReceiver() {

    private lateinit var playerActivity:PlayerActivity
    private lateinit var musicService:MusicService

    override fun onReceive(context: Context?, intent: Intent?) {

        playerActivity= PlayerActivity();
        musicService= MusicService()
        /***********************************TO RECIEVE THE ACTION FROM THE MUSIC SERVICE FOR NOTIFICATION****************/
        when(intent?.action){

            ApplicationClass.PREVIOUS-> Toast.makeText(context,"Previous Button Clicked",Toast.LENGTH_SHORT).show();
           ApplicationClass.PLAY-> { if(PlayerActivity.isPlaying==true) pauseMusic()
                else playMusic()
            }
            ApplicationClass.NEXT-> Toast.makeText(context,"Next Button Clicked",Toast.LENGTH_SHORT).show();
            ApplicationClass.EXIT-> {
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService=null;
                exitProcess(1)                   // Status code is 1 because we are willing closing the apllication
            }


        }

    }

    private fun playMusic(){
        PlayerActivity.isPlaying=true;
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotificaton(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying=false;
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotificaton(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
    }
}