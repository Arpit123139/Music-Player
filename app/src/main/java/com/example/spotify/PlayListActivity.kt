package com.example.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivityPlayListBinding

private lateinit var binding: ActivityPlayListBinding
class PlayListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding= ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}