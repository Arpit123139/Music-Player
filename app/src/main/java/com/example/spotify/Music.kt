package com.example.spotify

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

data class Music (val id:String,val title:String,val album:String,val artist:String,val duration:Long=0 ,val path:String,val artUri:String)

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