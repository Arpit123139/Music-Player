package com.example.spotify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.FavouriteViewBinding

class FavouriteAdapter(private val context: Context, private var musicList: ArrayList<Music>): RecyclerView.Adapter<FavouriteAdapter.MyHolder>() {

    class MyHolder(binding: FavouriteViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image=binding.songImgFV
        val name=binding.songNameFV

        val root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyHolder {                     // this function is called the same number of times that is equall to the total number of items in the screen and then the vies are recycled
        return MyHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder:MyHolder, position: Int) {
        holder.name.text=musicList[position].title
        Glide.with(context).load(musicList[position].artUri).apply(
            RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
            .into(holder.image)

        holder.root.setOnClickListener{
            val intent= Intent(context,PlayerActivity::class.java)
            // Adding some extra value via Intent
            intent.putExtra("index",position)
            intent.putExtra("Class","FavouriteAdapter")                        // To determine from which class the intent has arrived
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

}