package com.example.spotify

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotify.databinding.ActivityPlayListBinding
import com.example.spotify.databinding.AddPlaylistDialogBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

private lateinit var binding: ActivityPlayListBinding
private lateinit var adapter: PlaylistViewAdapter
class PlayListActivity : AppCompatActivity() {

    companion object{
        var musicPlaylist=MusicPlayList();
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding= ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*********************************************INITIALIZING THE ADAPTER****************************************/
        binding.playlistRV.setHasFixedSize(true)                 // It does not create extra object
        binding.playlistRV.setItemViewCacheSize(13)              // How many items are there in the cache
        binding.playlistRV.layoutManager= GridLayoutManager(this,2)
        adapter= PlaylistViewAdapter(this, musicPlaylist.ref)
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

    fun setDialogBtnBackground(context: Context, dialog: AlertDialog){
        //setting button text
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
        )
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
        )

        //setting button background
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
            MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
        )
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
            MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
        )
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@PlayListActivity).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        val dialog = builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD"){ dialog, _ ->
                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if(playlistName != null && createdBy != null)
                    if(playlistName.isNotEmpty() && createdBy.isNotEmpty())
                    {
                        addPlayList(playlistName.toString(), createdBy.toString())
                    }
                dialog.dismiss()
            }.create()
        dialog.show()
        setDialogBtnBackground(this, dialog)

    }

    private fun addPlayList(name: String, createdBy: String) {
        var playlistExists = false
        for(i in musicPlaylist.ref) {
            if (name == i.name){
                playlistExists = true
                break
            }
        }
        if(playlistExists) Toast.makeText(this, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {
            /*********************************Initializing the playList Class Created in Music.kt***/
            val tempPlaylist = PlayList()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            /***Formatting The Date************************************************************************************/
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)

            /****Adding the playList to the mainList which shows the data in the RecyclerView*****************************/
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlayList()
        }
    }
}