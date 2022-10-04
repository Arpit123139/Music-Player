package com.example.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivityFavoutiteBinding

private lateinit var binding:ActivityFavoutiteBinding

class FavoutiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding= ActivityFavoutiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}