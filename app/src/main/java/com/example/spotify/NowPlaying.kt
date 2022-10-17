package com.example.spotify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {

    companion object{
        lateinit var binding:FragmentNowPlayingBinding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding= FragmentNowPlayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE

        binding.playPauseBtnNP.setOnClickListener{
            if(PlayerActivity.isPlaying) PauseMusic() else PlayMusic()
        }

        binding.nextBtnNP.setOnClickListener{

            setSongPosition(true)
            // Now after the next song we want the media player to create again so next song is played
            PlayerActivity.musicService!!.createMediaPlayer()

            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(
                RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
                .into(NowPlaying.binding.songImgNP)

            binding.SongNameNP.text =PlayerActivity.musicListPA[PlayerActivity.songPosition].title;

            // When we change the song through now Fragment then it must be change in the notification  and vice versa is done in Notification Reciever
            PlayerActivity.musicService!!.showNotificaton(R.drawable.pause_icon)

            PlayMusic();
        }
        return view
    }

    /*****************************************onResume() is called whenever you navigate back to the activity from a call or something else**/
    /****************************************Basically when u click on a song you go to the player Activity and when u come baack u must see the now Playing Dialog***********/
    override fun onResume() {
        if(PlayerActivity.musicService!=null){
            binding.root.visibility=View.VISIBLE

            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(
                RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
                .into(binding.songImgNP)

            binding.SongNameNP.text =PlayerActivity.musicListPA[PlayerActivity.songPosition].title;
            if(PlayerActivity.isPlaying) binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            else binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)

        }
        super.onResume()
    }

    /************IMP --> As we play/pause through notification then it also reflect in the nowFragment so we must change in NotifivationReciever ****************/
    private fun PlayMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotificaton(R.drawable.pause_icon)          // As we play/pause through the nowFragment the effect must be visible in the notification as well as the player Activity
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying=true
    }
    private fun PauseMusic(){

        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotificaton(R.drawable.play_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying=false
    }


}