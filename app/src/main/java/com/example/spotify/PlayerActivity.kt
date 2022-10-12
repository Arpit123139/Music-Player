package com.example.spotify

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.ActivityPlayerBinding

//private lateinit var binding: ActivityPlayerBinding         We are making this as a companion object because we need it in Notification Reciever to make the icon change for play-Puse btn when the song stops from the notification
class PlayerActivity : AppCompatActivity(),ServiceConnection ,MediaPlayer.OnCompletionListener{

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

        /******************************************************SEEK BAR LISTENER**************************************/
        binding.seekBarPA.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // fromUser tell if the user had made changes
                if(fromUser)
                    musicService!!.mediaPlayer!!.seekTo(progress)           // It tells the media player which duration the song is played
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //NOT IMPLEMEMTING
                //USE  WHRN THE USER CLICKS ON IT
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //USE WHEN THE user Leave it
                //WE ARE NOT IMPLEMENTING IT...................................................................
            }
        })

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

    /*****************************Play The Audio    MAKING THIS FUNCTION GLOBALLY SO THAT IT CAN BE HANDLED BY THE notificationReciever for Play and Pause ***************************************************************************************/
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
            musicService!!.showNotificaton(R.drawable.pause_icon)

            /**************************************Setting the current time stamp of the song and the final Duration and the seekbar *****/
            binding.tvSeekBarStart.text= formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text= formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress= 0
            binding.seekBarPA.max= musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
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
            setSongPosition(increment)                    // This function is made Globally in Music.kt for the notfication next and prev button to work
            setLayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(increment)
            setLayout()
            createMediaPlayer()
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicService.MyBinder
        // take the current object reference of music Service
        musicService=binder.currentService()
        createMediaPlayer()
        //After the srvice is connected then start Foreground
       // musicService!!.showNotificaton(R.drawable.pause_icon)                   /*************This function call should be inside the creteMediaPlayer because as soon as the nextButton is clicked it creates a media Player but dont call the show Notification function so by default we put it inside  THIS IS FOR THE MAIN SCREEN

        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService=null
    }

    /*****************************************THIS FUNCTION HANDLES WHAT TO DO WHEN THE SONG COMPLETES AND PROGRESSBAR REACHES TO AN END*****/
    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        setLayout()
        createMediaPlayer()
    }
}