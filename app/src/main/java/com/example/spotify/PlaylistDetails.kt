package com.example.spotify

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    companion object{
        var currentPlayListpos:Int=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding= ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlayListpos= intent.extras?.getInt("index",-1)!!
        Toast.makeText(this,"${currentPlayListpos}",Toast.LENGTH_SHORT).show()
    }
}