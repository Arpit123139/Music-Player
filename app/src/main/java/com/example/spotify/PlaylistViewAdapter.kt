package com.example.spotify

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotify.databinding.PlaylistViewBinding

class PlaylistViewAdapter(private val context: Context, private var playlistList: ArrayList<String>): RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image=binding.playlistImg
        val name=binding.playlistName

        val root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyHolder {                     // this function is called the same number of times that is equall to the total number of items in the screen and then the vies are recycled
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder:MyHolder, position: Int) {
        holder.name.text=playlistList[position]
        holder.name.isSelected=true                         // For the moving Text

    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

}