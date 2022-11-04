package com.example.spotify

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

//private lateinit var binding: ActivityPlayerBinding         We are making this as a companion object because we need it in Notification Reciever to make the icon change for play-Puse btn when the song stops from the notification
class PlayerActivity : AppCompatActivity(),ServiceConnection ,MediaPlayer.OnCompletionListener{

    companion object{
        lateinit var musicListPA:ArrayList<Music>
        var songPosition:Int=0
//        var mediaPlayer:MediaPlayer?=null            We are removing this Media PLayer because we will use Service Media Player for the song to play in background
        var isPlaying:Boolean=false
        var musicService:MusicService?=null
        lateinit var binding: ActivityPlayerBinding

        var repeat:Boolean=false

        var min15:Boolean=false
        var min30:Boolean=false
        var min60:Boolean=false

        // tell the current song playing id
        var nowPlayingId:String=""

        var isFavourite:Boolean=false
        var fIndex:Int=-1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding= ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /********************************************Starting The Service We are removing the code from here because when the NowFragment is clicked we dont want the service to start again so we are starting the service individually in the initializeLayout()   *********************************************/
//        val intent= Intent(this,MusicService::class.java)
//        bindService(intent,this, BIND_AUTO_CREATE)
//        startService(intent)

        /**************************************If It Arrives Through Intent Chooser***************************/
        if(intent.data?.scheme.contentEquals("content")){

            val intentService= Intent(this,MusicService::class.java)
            bindService(intentService,this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPA= ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))

            //getImgArt function created in Music.kt
            Glide.with(this).load(getImgArt(musicListPA[songPosition].path)).apply(RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
                .into(binding.songImgPA)

            binding.songNamePA.text= musicListPA[songPosition].title


        }else IntializeLayout()


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

        /*****************************************FOR REPEAT BUTTON*****************************************************/
        binding.repeatBtnPA.setOnClickListener{
            if(!repeat){
                repeat=true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            }else{
                repeat=false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }

        }

        /******************************************BACK BUTTON*********************************************************/
        binding.backBtnPA.setOnClickListener{
            finish()
        }

        /**************************************** SETTING UP THE EQUALIZER BUTTON*************************************/
        binding.equalizerBtnPA.setOnClickListener{

            try {
                val intent=Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)             //built in Equalizer
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)

                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)               // If we does not set this then it will change the audio of the whole phone
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(intent,13)
            }catch (e:Exception){
                Toast.makeText(this,"Equalizer feature Not supported",Toast.LENGTH_SHORT).show()
            }
        }

        /*********************************************TIMER BUTTON FUNCTIONALITY***********************************/
        binding.timerBtnPA.setOnClickListener{
            val timer=min15 || min30 || min60
            if(!timer) showBottomSheetDialog()                                //    Whrn no time is selected
            else{
                val builder= MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to Stop Timer ?")
                    .setPositiveButton("Yes"){_,_->
                        min15=false;
                        min30=false;
                        min60=false
                        binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
                    }
                    .setNegativeButton("No"){dialog,_->
                        dialog.dismiss()
                    }
                val customDialog=builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

        /*********************************************Sharing song FILE SHARING***************************************************/
        binding.shareBtnPA.setOnClickListener{
            val shareIntent=Intent()
            shareIntent.action=Intent.ACTION_SEND                    // What does the intent do
            shareIntent.type="audio/*"                                //      /* MEANS ant extension
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing Music File !!"))           // Implements a chooser whether we have to share from whatsapp /email or anything
         }

        /**************************Handling the favourite button***********************************************************/
        binding.favouriteBtnPA.setOnClickListener{
            if(isFavourite){
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon);
                isFavourite=false
                //Removing from the favourite list
                FavoutiteActivity.favouriteSongs.removeAt(fIndex)
            }else{
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon);
                isFavourite=true
                //Add the song to the List
                FavoutiteActivity.favouriteSongs.add(musicListPA[songPosition])
            }
        }
    }

    /*********************************************It will give the detail of the song WHEN CHOOSEN RANDOMLY FROM STORAGE AND BE PLAYED IN OUR PLAYER *********************************/
    private fun getMusicDetails(contentUri: Uri): Music {

        // As It is A single song we cannot access the whole data
        var cursor:Cursor?=null
        try {
            val projection= arrayOf(MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.TITLE)
            cursor=this.contentResolver.query(contentUri,projection,null,null,null)

            val dataColoumn=cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColoumn=cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val titleColumn=cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

            cursor!!.moveToFirst()
            val title=titleColumn?.let { cursor?.getString(it) }
            val path= dataColoumn?.let { cursor?.getString(it) }
            val duration=durationColoumn?.let { cursor.getLong(it) }

            return Music("Unknown", title = title.toString(),"Unknown","Unknown",duration!!, artUri = "Unknown", path = path.toString())
        }finally {
            cursor?.close()
        }
    }

    private fun IntializeLayout(){

        /***********************************************************Catching the intent from MusicAdapter*******************************************/
        songPosition=intent.getIntExtra("index",0)
        when(intent.getStringExtra("Class")){
            "MusicAdapter"->{
                val intent= Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)

                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                Toast.makeText(this,"Enter the Music Adapter", Toast.LENGTH_SHORT).show()
                /***********************************************SETTING THE LAYOUT WHEN THE SONG IS CLICKED ***********************************************/
                setLayout()
            }
            /************************************************SHUFFLE FUNCTIONALITY*******************************************************************/
            "MainActivity"->{
                val intent= Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)

                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()

            }
            "MusicAdapterSearch"->{
                val intent= Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListSearch)
                setLayout()
            }

            "NowPlaying"->{
                setLayout()
                /************Here we have to initialize all the views because we are not starting the Activity Again **************/
                binding.tvSeekBarStart.text= formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text=formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress= musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max= musicService!!.mediaPlayer!!.duration

                if(isPlaying) binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
                else binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            }

            "FavouriteAdapter"->{
                val intent= Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(FavoutiteActivity.favouriteSongs)
                setLayout()
            }
            "FavoriteShuffle"->{

                val intent= Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(FavoutiteActivity.favouriteSongs)
                musicListPA.shuffle()
                setLayout()
            }

            "PlayListDetailsAdapter"->{
                val intent= Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlayListpos].playlist)
                setLayout()
            }

            "PlayListDetailsShuffle"->{
                val intent= Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlayListpos].playlist)
                musicListPA.shuffle()
                setLayout()
            }

        }
    }

    /***********************************************SETTING THE LAYOUT WHEN THE SONG IS CLICKED ***********************************************/
    private fun setLayout(){
        /**********************************Adding code for the favourite Song FOR FILLING THE FAVOURITE BUTTON***************************************/
        fIndex= favouriteChecker(musicListPA[songPosition].id)
        if(isFavourite)
        {
            binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        }else{
            binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
        }
        Glide.with(this).load(musicListPA[songPosition].artUri).apply(RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
            .into(binding.songImgPA)

        binding.songNamePA.text= musicListPA[songPosition].title
        if(repeat){
            binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
        }
        if(min15 || min30 ||min60){
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
        }
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
            musicService!!.showNotificaton(R.drawable.pause_icon,1f)

            /**************************************Setting the current time stamp of the song and the final Duration and the seekbar *****/
            binding.tvSeekBarStart.text= formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text= formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress= 0
            binding.seekBarPA.max= musicService!!.mediaPlayer!!.duration
            /*****************this code is added to make sure when the current song is playing and we click on the same song again thaen it does not begin from start This same function is created in musicService.CreateMediaPlayer because from there as well we create a PlayerActivity *********/
            nowPlayingId= musicListPA[songPosition].id

            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        }catch (e:Exception){
            return
        }
    }

    /********************************************************PLAY MUSIC*************************************************************************/
    private fun playMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotificaton(R.drawable.pause_icon,1f)
        isPlaying=true
        musicService!!.mediaPlayer!!.start()
    }

    /***********************************************************PAUSE MUSIC**********************************************************************/
    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        musicService!!.showNotificaton(R.drawable.play_icon,0f)
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

        /******This Library is added so that if the call arrive than the song must stop and call should continue*******/
        musicService!!.audioManager=getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)         // JAB FOCUS MIL JAAEGA TOH AUDIO KO PLAY KAR SAKTE HAIN
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== 13  || resultCode== RESULT_OK){
            return;
        }
    }

    /******************************************SETTING UP BOTTOM SHEET DIALOG FOR TIMER********************************/
    private fun showBottomSheetDialog(){
        val dialog=BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()

        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop in 15 min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))

            min15=true;
            /*********************There is no mthod to stop the thread directly so everytime we ar checking it *********************/
            // 60000 ms is equal to 1 min
            Thread{Thread.sleep(15*60000)                             // After every 15 min it will check the min15 variable
            if(min15) exitApplication()}.start()                       // This function is made in Music.kt
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop in 30 min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))

            min30=true;
            Thread{Thread.sleep(30*60000)
                if(min30) exitApplication()}.start()                       // This function is made in Music.kt
            dialog.dismiss()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop in 60 min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))

            min60=true;
            Thread{Thread.sleep(60*60000)
                if(min60) exitApplication()}.start()                       // This function is made in Music.kt
            dialog.dismiss()
            dialog.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if(musicListPA[songPosition].id=="Unknown" && isPlaying) exitApplication()
    }
}