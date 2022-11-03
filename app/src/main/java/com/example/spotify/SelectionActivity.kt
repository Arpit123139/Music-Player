package com.example.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotify.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])

        binding.selectionRV.setItemViewCacheSize(18);
        binding.selectionRV.layoutManager= LinearLayoutManager(this);
        adapter= MusicAdapter(this,MainActivity.MusicListMA,false,true)
        binding.selectionRV.adapter=adapter

        /*********************************************Search Functionality********************************************/
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // User press the enters button
                return true;
            }

            override fun onQueryTextChange(newText: String?): Boolean {      // newText is the text that we are typping
                //When the user is typing then only the search results are visible
                MainActivity.MusicListSearch = ArrayList()
                if(newText!=null){
                    val userInput=newText.lowercase()
                    for(song in MainActivity.MusicListMA){
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.MusicListSearch.add(song)                        // we are adding song in the musicListSearch which matched with the userInput
                    }
                    MainActivity.search =true
                    adapter.updateMusicList(MainActivity.MusicListSearch)            // we are changing the list of music  that must be visible in the mainScreen  in the recyclerView
                }
                return true;
            }
        })

        binding.backBtnSA.setOnClickListener{
            finish()
        }
    }
}