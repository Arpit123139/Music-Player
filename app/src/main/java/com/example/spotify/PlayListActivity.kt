package com.example.spotify

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotify.databinding.ActivityPlayListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private lateinit var binding: ActivityPlayListBinding
private lateinit var adapter: PlaylistViewAdapter
class PlayListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding= ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*********************************************INITIALIZING THE ADAPTER****************************************/
        binding.playlistRV.setHasFixedSize(true)                 // It does not create extra object
        binding.playlistRV.setItemViewCacheSize(13)              // How many items are there in the cache

        val tempList=ArrayList<String>();
        tempList.add("Travel Songs")
        tempList.add("Lets Enjoy the music")
        tempList.add("Travel Songs For Train")
        tempList.add("Travel Songs For bus")
        binding.playlistRV.layoutManager= GridLayoutManager(this,2)
        adapter= PlaylistViewAdapter(this, tempList)
        binding.playlistRV.adapter=adapter
       /****************************************************************************************************************/
     binding.backBtnPLA.setOnClickListener{
            finish()
        }
/*******************************************************ADD PLAYLIST BUTTON***********************************************/
        binding.addPlaylistBtn.setOnClickListener{
            customAlertDialog()
        }
    }

    private fun customAlertDialog(){
        val customDialog=LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog, binding.root,false);

        val builder= MaterialAlertDialogBuilder(this)
        builder.setView(customDialog).setTitle("Playlist Detail")
            .setPositiveButton("Add"){dialog,_->
                dialog.dismiss()
            }.show()


    }
}