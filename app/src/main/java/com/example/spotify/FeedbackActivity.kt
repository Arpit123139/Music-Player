package com.example.spotify

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.coolPinkNav)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Feedback"


        val feedbackMsg=binding.feedbackMsgFA.text.toString()+"\n"+binding.emailFA.text.toString()
        val subject=binding.topicFA.text.toString()
        binding.sendFA.setOnClickListener {
            Toast.makeText(this,feedbackMsg,Toast.LENGTH_LONG).show()
        }
    }
}