package com.example.spotify

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.ActivityPlayerBinding

//private lateinit var binding: ActivityPlayerBinding         We are making this as a companion object because we need it in Notification Reciever to make the icon change for play-Puse btn when the song stops from the notification
class PlayerActivity : AppCompatActivity(),ServiceConnection {

    companion object{
        lateinit var musicListPA:ArrayList<Music>
        var songPosition:Int=0
//        var mediaPlayer:MediaPlayer?=null            We are removing this Media PLayer because we will use Service Media Player for the song to play in background
        var isPlaying:Boolean=false
        var musicService:MusicService?=null
        lateinit var binding: ActivityPlayerBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding= ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /********************************************Starting The Service*********************************************/
        val intent= Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)

        IntializeLayout()


        /*********************************************PLAY-PAUSE BTN************************************************************************/
        binding.playPauseBtnPA.setOnClickListener{

            if(isPlaying) pauseMusic()
            else playMusic()
        }


        /*******************************************************PREVIOUS BUTTON****************************************************************/
        binding.previousBtnPA.setOnClickListener{
            prevNextSong(false)
        }
        /*******************************************************NEXT BUTTON****************************************************************/
        binding.nextBtnPA.setOnClickListener{
            prevNextSong(true)
        }

    }

    private fun IntializeLayout(){

        /***********************************************************Catching the intent from MusicAdapter*******************************************/
        songPosition=intent.getIntExtra("index",0)
        when(intent.getStringExtra("Class")){
            "MusicAdapter"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                Toast.makeText(this,"Enter the Music Adapter", Toast.LENGTH_SHORT).show()
                /***********************************************SETTING THE LAYOUT WHEN THE SONG IS CLICKED ***********************************************/
                setLayout()
                /*****************************Play The Audio***************************************************************************************/
                //by defaqult Class



            }
            /************************************************SHUFFLE FUNCTIONALITY*******************************************************************/
            "MainActivity"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()

            }
        }
    }

    /***********************************************SETTING THE LAYOUT WHEN THE SONG IS CLICKED ***********************************************/
    private fun setLayout(){
        Glide.with(this).load(musicListPA[songPosition].artUri).apply(RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
            .into(binding.songImgPA)

        binding.songNamePA.text= musicListPA[songPosition].title
    }

    /*****************************Play The Audio***************************************************************************************/
    private fun createMediaPlayer(){
        try {
            if(musicService!!.mediaPlayer==null)
                musicService!!.mediaPlayer= MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying=true
            binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        }catch (e:Exception){
            return
        }
    }

    /********************************************************PLAY MUSIC*************************************************************************/
    private fun playMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotificaton(R.drawable.pause_icon)
        isPlaying=true
        musicService!!.mediaPlayer!!.start()
    }

    /***********************************************************PAUSE MUSIC**********************************************************************/
    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        musicService!!.showNotificaton(R.drawable.play_icon)
        isPlaying=false
        musicService!!.mediaPlayer!!.pause()

    }

    /************************************************************PREV NEXT MUSIC******************************************************************/
    private fun prevNextSong(increment:Boolean){

        if(increment)
        {
            setSongPosition(increment)
            setLayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(increment)
            setLayout()
            createMediaPlayer()
        }
    }
    /******************************************************************SET SONG POSITION****************************************************************/
    private fun setSongPosition(increment:Boolean){

        if(increment){
            if(musicListPA.size-1== songPosition)
                songPosition=0
            else
                ++songPosition
        }
        else{
            if(songPosition==0)
                songPosition= musicListPA.size-1
            else
                --songPosition

        }

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicService.MyBinder
        // take the current object reference of music Service
        musicService=binder.currentService()
        createMediaPlayer()
        //After the srvice is connected then start Foreground
        musicService!!.showNotificaton(R.drawable.pause_icon)

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService=null
    }
}