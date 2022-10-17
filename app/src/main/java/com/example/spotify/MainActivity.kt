package com.example.spotify

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotify.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import kotlin.system.exitProcess



class MainActivity : AppCompatActivity() {

    //Binding helps to connect the xml layout to the activity.There Are two types of Binding ViewBinding and DataBinding
    // we must add the permission in the build.gradle under android box
    private lateinit var binding:ActivityMainBinding
    private lateinit var toggle:ActionBarDrawerToggle

    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var MusicListMA:ArrayList<Music>

        /*******************************************THIS LIST IS FOR THE SONG SEARCH************************************/
        lateinit var MusicListSearch:ArrayList<Music>

        var search:Boolean=false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestRuntimePermission()
        setTheme(R.style.coolPinkNav)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        /********************************************For Nav Drawer****************************************************/
        toggle=ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)                         // We are passing binding.root we can pass the id as well but as the root element is itself a drawer so we are directly passing that
        // Attaching toogle to the root LAYOUT
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // We have to override the function to show our drawer Layout
        /*******************************************THE ABOVE CODE SHOULD WORK WHETHER OR NOT THE PERMISSION IS ALLOWED ***********************************************************************/


        if(requestRuntimePermission())    /**************************THE BELOW CODE SHOULD ONLY BE EXECUTED WHEN PERMISIION IS ALLOWED ELSE THE APP WILL BE CRASHED*/
        {
            search=false
            /************************TO ACCESS ALL THE MEDIA FILES AND CREATING LIST ****************************************************************************/
            MusicListMA=getAllAudio()         //This function is created below
            /******************************************************************************************************************/

            /***********************************************SETTING MUSIC ADAPTER**********************************************/
            binding.musicRV.setHasFixedSize(true)                 // It does not create extra object
            binding.musicRV.setItemViewCacheSize(13)              // How many items are there in the cache

            binding.musicRV.layoutManager=LinearLayoutManager(this@MainActivity)
            musicAdapter= MusicAdapter(this@MainActivity, MusicListMA)
            binding.musicRV.adapter=musicAdapter
            binding.totalSongs.text="Total Songs : "+ MusicListMA.size.toString()
            /*********************************************************************************************************************/
        }



        /********************************************8SETTING UP A ALERT DIALOG BOX FOR EXIT OPTION*************************/
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navFeedback->Toast.makeText(this,"Feedback Clicked",Toast.LENGTH_SHORT).show()

                R.id.navAbout->Toast.makeText(this,"About Clicked",Toast.LENGTH_SHORT).show()

                R.id.navSettings->Toast.makeText(this,"Settings Clicked",Toast.LENGTH_SHORT).show()

                R.id.navExit-> {
                    val builder=MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close App")
                        .setPositiveButton("Yes"){_,_->
                            if(PlayerActivity.musicService!=null){                  /*************This Condition is there because when there is No Foreground Service running that is initially withouth clicking any song then no need to stop foreground service **/
                                PlayerActivity.musicService!!.stopForeground(true)
                                PlayerActivity.musicService!!.mediaPlayer!!.release()            // The resources that are occupied by the media Player must be released
                                PlayerActivity.musicService=null;
                            }
                            exitProcess(1)
                        }
                        .setNegativeButton("No"){dialog,_->
                            dialog.dismiss()
                        }
                    val customDialog=builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

                }                 // This indicate that we want to close the application Knowingly
            }
            // this is a method to write a return statement
            true
        }

        binding.shuffleBtn.setOnClickListener{
            //Intents help us to move from one Activity to Another
            val intent= Intent(this@MainActivity,PlayerActivity::class.java)
            /**************************************SHUFFLE BUTTON FUNCTIONALITY****************************************************************/
            intent.putExtra("index",0)
            intent.putExtra("Class","MainActivity")                        // To determine from which class the intent has arrived
            startActivity(intent)
        }
        binding.favouriteBtn.setOnClickListener{
            val intent= Intent(this@MainActivity,FavoutiteActivity::class.java)
            startActivity(intent)
        }
        binding.playlistBtn.setOnClickListener {
            val intent= Intent(this@MainActivity,PlayListActivity::class.java)
            startActivity(intent)
        }
    }

    //For requesting permission it will return true and false
    private fun requestRuntimePermission() :Boolean{
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
        }
    }
    /************************************************************************ For Nav Drawer******************************************************/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    /************************************************ACCESS MEDIA FROM STORAGE******************************************************************/
    @SuppressLint("Range")
    private fun getAllAudio():ArrayList<Music>{
        val tempList=ArrayList<Music>()
        // what type of file we want we have to tell to the cursor
        val selection= MediaStore.Audio.Media.IS_MUSIC +" !=0"                   // file should not be null
        // What type of data we want from the Song
        val projection=arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)        // tHE second Last Parameter is to sort it according to the Date

        // cursor
        val cursor=this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,MediaStore.Audio.Media.DATE_ADDED +" DESC",null)

        if(cursor!=null)
            if(cursor.moveToFirst())                                // move it to the first Position
                do{
                    //To access all the audio file
                    var titleC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    var idC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    var albumC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    var artistC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    var pathC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    var durationC=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val uri = Uri.parse("content://media/external/audio/albumart")                   // This String is ver Important
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    val music=Music(id=idC,title=titleC,album=albumC,artist=artistC,path=pathC, duration = durationC, artUri =artUriC)
                    val file= File(music.path)
                    if(file.exists())                    // To avoid the app to crash
                        tempList.add(music)

                }while (cursor.moveToNext())                        // till the items are null or it return false
                 cursor?.close()




        return tempList
    }

    /********************************NOTIFICATION GET REMOVED WHEN WE REMOVE THE ACTIVITY********************************/
    override fun onDestroy() {
        super.onDestroy()
        /********************THIS CONDITION IS WHEN THE USER STARTED THE SONG THEN PAUSE IT AND TRY TO REMOVE THE ACTIVITY ********************/
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService!=null){
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()            // The resources that are occupied by the media Player must be released
            PlayerActivity.musicService=null;
            exitProcess(1)
        }
    }

    /******************************************************For Creating the search View in Menu*************************/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_view_menu,menu);
        val searchView=menu?.findItem(R.id.search_view)?.actionView as androidx.appcompat.widget.SearchView
        /********************************TAKING refernce of the search view and Handling OnQueryTextListner******************/
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // User press the enters button
                return true;
            }

            override fun onQueryTextChange(newText: String?): Boolean {      // newText is the text that we are typping
                //When the user is typing then only the search results are visible
                MusicListSearch= ArrayList()
                if(newText!=null){
                    val userInput=newText.lowercase()
                    for(song in MusicListMA){
                        if(song.title.lowercase().contains(userInput))
                            MusicListSearch.add(song)                        // we are adding song in the musicListSearch which matched with the userInput
                    }
                    search=true
                    musicAdapter.updateMusicList(MusicListSearch)            // we are changing the list of music  that must be visible in the mainScreen  in the recyclerView
                }
                return true;
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


}