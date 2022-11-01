package com.example.spotify

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music (val id:String,val title:String,val album:String,val artist:String,val duration:Long=0 ,val path:String,val artUri:String)

/********************************************************CLASS FOR THE PLAYLIST***************************************/
class PlayList{
    lateinit var name:String
    lateinit var playlist:ArrayList<Music>
    lateinit var createdBy:String
    lateinit var createdOn:String
}
// We can take the direct reference but during storage in Shared Preference it may create some problem
class MusicPlayList{
    var ref:ArrayList<PlayList> = ArrayList()
}
/**********************************************************************************************************************************/
fun formatDuration(duration:Long):String{

    val minutes=TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)                  // It will convert the milliseconds into minutes
    val seconds=(TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))   // removing the seconds that can be converted into the minutes

    return String.format("%02d:%02d",minutes,seconds)                // %2 specifies the length d specifies integer
}

fun getImgArt(path:String): ByteArray? {                        // We cannot set image using Glide in the notificaion we need a BitMap icon for it
    // this function will give a image of particulat song whose path we will pass
    val retriever=MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

/******************************************************************SET SONG POSITION****************************************************************/
 fun setSongPosition(increment:Boolean){

    /**********************************If the repeat is true then the song position will not change********************/
    if(!PlayerActivity.repeat){
        if(increment){
            if(PlayerActivity.musicListPA.size-1== PlayerActivity.songPosition)
                PlayerActivity.songPosition =0
            else
                ++PlayerActivity.songPosition
        }
        else{
            if(PlayerActivity.songPosition ==0)
                PlayerActivity.songPosition = PlayerActivity.musicListPA.size-1
            else
                --PlayerActivity.songPosition

        }
    }


}

/*************************************For Closing the Activity*********************************************/
fun exitApplication(){
    if(PlayerActivity!=null){

        /******************To Release The Resources When The Application Closes ************************/
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)

        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService=null;
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        exitProcess(1)
    }

}
/***************************************Global FUNCTION TO CHECK THE SONG IS FAVOURITE OFR NOT************************/
fun favouriteChecker(id: String):Int{
    PlayerActivity.isFavourite=false;
    FavoutiteActivity.favouriteSongs.forEachIndexed{index,music->
        if(id==music.id){
            PlayerActivity.isFavourite=true;
            return index;
        }

    }
    return -1;
}

/*********************************************If by chance the user deletes the file from the storage then it will not be deleted from the Favourites and Playlist so we check here whether it exsist in file or not*********/
fun checkPlayList(playlist:ArrayList<Music>):ArrayList<Music>{

    playlist.forEachIndexed { index, music ->
        val file= File(music.path)
        if(!file.exists())
            playlist.removeAt(index);
    }
    return playlist
}

