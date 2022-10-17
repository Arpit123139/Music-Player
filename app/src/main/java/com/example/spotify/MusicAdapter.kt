package com.example.spotify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, private var musicList: ArrayList<Music>):RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    class MyHolder(binding: MusicViewBinding) :RecyclerView.ViewHolder(binding.root) {

        val title=binding.songNameMV
        val album=binding.songAlbumMV
        val image=binding.imageMV
        val duration=binding.songDuration
        var root=binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {                     // this function is called the same number of times that is equall to the total number of items in the screen and then the vies are recycled
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        holder.title.text=musicList[position].title                   // It functions is to bind when it gets the data
        holder.album.text=musicList[position].album
        holder.duration.text= formatDuration(musicList[position].duration)
        Glide.with(context).load(musicList[position].artUri).apply(RequestOptions().placeholder(R.drawable.music).centerCrop())        // If the image does not load properly
            .into(holder.image)
        /********************If somebody click on the item********************************************/
        holder.root.setOnClickListener{
//            val intent= Intent(context,PlayerActivity::class.java)
//            // Adding some extra value via Intent
//            intent.putExtra("index",position)
//            intent.putExtra("Class","MusicAdapter")                        // To determine from which class the intent has arrived
//            ContextCompat.startActivity(context,intent,null)
            when{
                MainActivity.search->sendIntent("MusicAdapterSearch",position)         //We are sending the different intent when we enable Search  because the list of song tha are visible in the main Screen are store in MusicListSearch
                else->sendIntent("MusicAdapter",position)
            }

        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>){
        musicList= ArrayList();
        musicList.addAll(searchList);
        notifyDataSetChanged()
    }

    private fun sendIntent(ref:String,pos:Int){
        val intent= Intent(context,PlayerActivity::class.java)
        // Adding some extra value via Intent
        intent.putExtra("index",pos)
        intent.putExtra("Class",ref)                        // To determine from which class the intent has arrived
        ContextCompat.startActivity(context,intent,null)
    }
}