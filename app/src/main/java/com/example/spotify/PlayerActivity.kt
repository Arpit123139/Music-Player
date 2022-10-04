package com.example.spotify

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.ActivityPlayerBinding

private lateinit var binding: ActivityPlayerBinding
class PlayerActivity : AppCompatActivity() {

    companion object{
        lateinit var musicListPA:ArrayList<Music>
        var songPosition:Int=0
        var mediaPlayer:MediaPlayer?=null
        var isPlaying:Boolean=false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding= ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        /***********************************************************Catching the intent from MusicAdapter*******************************************/
        songPosition=intent.getIntExtra("index",0)
        when(intent.getStringExtra("Class")){
            "MusicAdapter"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                /***********************************************SETTING THE LAYOUT WHEN THE SONG IS CLICKED ***********************************************/
                setLayout()
                /*****************************Play The Audio***************************************************************************************/
                //by defaqult Class
                createMediaPlayer()


            }
            /************************************************SHUFFLE FUNCTIONALITY*******************************************************************/
            "MainActivity"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createMediaPlayer()
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
            if(mediaPlayer==null)
                mediaPlayer= MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying=true
            binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        }catch (e:Exception){
            return
        }
    }

    /********************************************************PLAY MUSIC*************************************************************************/
    private fun playMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        isPlaying=true
        mediaPlayer!!.start()
    }

    /***********************************************************PAUSE MUSIC**********************************************************************/
    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        isPlaying=false
        mediaPlayer!!.pause()

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
}