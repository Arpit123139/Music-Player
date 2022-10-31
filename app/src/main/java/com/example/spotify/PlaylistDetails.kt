package com.example.spotify

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter
    companion object{
        var currentPlayListpos:Int=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding= ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlayListpos= intent.extras?.getInt("index",-1)!!
       // Toast.makeText(this,"${currentPlayListpos}",Toast.LENGTH_SHORT).show()

        binding.playDetailRV.setItemViewCacheSize(18);
        binding.playDetailRV.layoutManager=LinearLayoutManager(this);
        PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist.addAll(MainActivity.MusicListMA);
        adapter= MusicAdapter(this,PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist,true)
        binding.playDetailRV.adapter=adapter

    }

    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text=PlayListActivity.musicPlaylist.ref[currentPlayListpos].name
        binding.moreInfoPD.text="Total ${adapter.itemCount} Songs.\n\n"+
                "Created On:\n${PlayListActivity.musicPlaylist.ref[currentPlayListpos].createdOn}\n\n"+
                "-- ${PlayListActivity.musicPlaylist.ref[currentPlayListpos].createdBy}"

        if(adapter.itemCount>0){

            Glide.with(this)
                .load(PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
                .into(binding.playlistImgPD)

            binding.shuffleBtnPD.visibility= View.VISIBLE
        }
    }
}