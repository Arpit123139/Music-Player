package com.example.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.coolPinkNav)

        supportActionBar?.title="Settings"
    }
}