package com.example.spotify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding=ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title="Feedback"

        binding.sendFA.setOnClickListener {
//            val feedbackMsg=binding.feedbackMsgFA.text.toString()+"\n"+binding.emailFA.text.toString()
            val subject=binding.topicFA.text.toString().trim()
            val feedback=binding.feedbackMsgFA.text.toString().trim()
            /*************************************************SENDING THE MAIL***************************************/
            val userName="1ms20is023@gmail.com"                              // to Email Address To Whom the email is to be sent
            if(feedback.isNotEmpty() && subject.isNotEmpty()){

                val mailId=userName.trim()

                var i=Intent(Intent.ACTION_SEND)
                i.data = Uri.parse("Mail to: ")
                i.type ="text/plain"

                i.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailId))                  // Recievr Email Address
                i.putExtra(Intent.EXTRA_SUBJECT,subject)
                i.putExtra(Intent.EXTRA_TEXT,feedback)

                startActivity(Intent.createChooser(i,"Choose Email Client"))

            }
            else{
                Toast.makeText(this,"Went Something Wrong",Toast.LENGTH_LONG).show()
            }

        }
    }
}