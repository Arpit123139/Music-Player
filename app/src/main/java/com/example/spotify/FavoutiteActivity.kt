package com.example.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotify.databinding.ActivityFavoutiteBinding

private lateinit var binding:ActivityFavoutiteBinding
private lateinit var adapter: FavouriteAdapter


class FavoutiteActivity : AppCompatActivity() {

    companion object{
        var favouriteSongs: ArrayList<Music> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding= ActivityFavoutiteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        

        binding.favouriteRV.setHasFixedSize(true)                 // It does not create extra object
        binding.favouriteRV.setItemViewCacheSize(13)              // How many items are there in the cache
        binding.favouriteRV.layoutManager= GridLayoutManager(this,4)
        adapter= FavouriteAdapter(this, favouriteSongs)
        binding.favouriteRV.adapter=adapter



      binding.backBtnFA.setOnClickListener{
            finish()
        }

    }
}