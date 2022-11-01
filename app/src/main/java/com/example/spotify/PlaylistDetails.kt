package com.example.spotify

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

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
        /********************Checking if the song is not deleted from the Storage****************************************/
        PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist= checkPlayList(PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist)

        binding.playDetailRV.setItemViewCacheSize(18);
        binding.playDetailRV.layoutManager=LinearLayoutManager(this);
//      Adding Dummy Data Initially
//        PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist.addAll(MainActivity.MusicListMA);
        adapter= MusicAdapter(this,PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist,true)
        binding.playDetailRV.adapter=adapter

        /**********************************Back Button Functionality******************************************/
        binding.backBtnPD.setOnClickListener{
            finish()
        }

        /*********************************************SHUFFLE BUTTON FUNCTIONALITY*************************************/
        binding.shuffleBtnPD.setOnClickListener{
            val intent= Intent(this,PlayerActivity::class.java)
            /**************************************SHUFFLE BUTTON FUNCTIONALITY****************************************************************/
            intent.putExtra("index",0)
            intent.putExtra("Class","PlayListDetailsShuffle")                        // To determine from which class the intent has arrived
            startActivity(intent)
        }

        /***********************************When we click the add button**************************/
        binding.AddBtnPD.setOnClickListener{
            startActivity(Intent(this,SelectionActivity::class.java))

        }

        /****************************Remove All Functionality********************************************/
        binding.RemoveAllPD.setOnClickListener {
            val builder= MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from PlayList")
                .setPositiveButton("Yes"){dialog,_->

                    PlayListActivity.musicPlaylist.ref[currentPlayListpos].playlist.clear();
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
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

        adapter.notifyDataSetChanged()              // When the songs are added

        /*********************************************FOR STORING FAVOURITES DATA USING SHARED PREFERENCES**************/
        val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlayList= GsonBuilder().create().toJson(PlayListActivity.musicPlaylist);    // Converting favouriteSong list into jsonString
        editor.putString("MusicPlaylist",jsonStringPlayList)            // Store in the key value pair....
        editor.apply()

    }
}