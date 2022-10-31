package com.example.spotify

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spotify.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistViewAdapter(private val context: Context, private var playlistList: ArrayList<PlayList>): RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image=binding.playlistImg
        val name=binding.playlistName
        val root=binding.root
        val delete=binding.playlistDeleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyHolder {                     // this function is called the same number of times that is equall to the total number of items in the screen and then the vies are recycled
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder:MyHolder, position: Int) {
        holder.name.text=playlistList[position].name
        holder.name.isSelected=true                         // For the moving Text

        //When we press the delete button
        holder.delete.setOnClickListener{
            /**************************Alert Dialog Box*****************************************************/
            val builder= MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to Delete Playlist")
                .setPositiveButton("Yes"){dialog,_->
                    PlayListActivity.musicPlaylist.ref.removeAt(position);              // It stores all the Playlist
                    /*****************************playlistList.removeAt(position) cannot do this because it is not the main list which is storing the playlist and also it is not refering to it A new Variable is created inside adapter****/
                    refreshPlayList()
                    dialog.dismiss()
                }

            val customDialog=builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

        /***********************************************When we click on the PlayList Itself********************************/
        holder.root.setOnClickListener{

            val intent=Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null);
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refreshPlayList(){
        playlistList= ArrayList();
        playlistList.addAll(PlayListActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}