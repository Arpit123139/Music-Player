package com.example.spotify

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])

        binding= ActivityFavoutiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /********************Checking if the song is not deleted from the Storage****************************************/
        favouriteSongs= checkPlayList(favouriteSongs)

        binding.favouriteRV.setHasFixedSize(true)                 // It does not create extra object
        binding.favouriteRV.setItemViewCacheSize(13)              // How many items are there in the cache
        binding.favouriteRV.layoutManager= GridLayoutManager(this,4)
        adapter= FavouriteAdapter(this, favouriteSongs)
        binding.favouriteRV.adapter=adapter

        //Hiding the shuffle button when there is no song to display in the FavouriteActivity
        if(favouriteSongs.size<1){
            binding.shuffleBtnFA.visibility= View.INVISIBLE
        }



      binding.backBtnFA.setOnClickListener{
            finish()
        }
        /************************************************SHUFFLE BUTTON**********************************************/
        binding.shuffleBtnFA.setOnClickListener{
            val intent= Intent(this,PlayerActivity::class.java)
            /**************************************SHUFFLE BUTTON FUNCTIONALITY****************************************************************/
            intent.putExtra("index",0)
            intent.putExtra("Class","FavoriteShuffle")                        // To determine from which class the intent has arrived
            startActivity(intent)
        }

    }
}