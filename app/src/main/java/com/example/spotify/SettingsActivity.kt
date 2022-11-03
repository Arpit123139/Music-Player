package com.example.spotify

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        when(MainActivity.themeIndex){
            0->binding.coolPinkTheme.setBackgroundColor(Color.YELLOW)
            1->binding.coolBlueTheme.setBackgroundColor(Color.YELLOW)
            2->binding.coolPurpleTheme.setBackgroundColor(Color.YELLOW)
            3->binding.coolGreenTheme.setBackgroundColor(Color.YELLOW)
            4->binding.coolBlackTheme.setBackgroundColor(Color.YELLOW)
        }
        supportActionBar?.title="Settings"

        binding.coolPinkTheme.setOnClickListener{
            saveTheme(0)
        }
        binding.coolBlueTheme.setOnClickListener{
            saveTheme(1)
        }
        binding.coolPurpleTheme.setOnClickListener{
            saveTheme(2)
        }
        binding.coolGreenTheme.setOnClickListener{
            saveTheme(3)
            Toast.makeText(this,"fvvfv",Toast.LENGTH_SHORT).show()
        }
        binding.coolBlackTheme.setOnClickListener{
            saveTheme(4)
        }

        binding.versionName.text=setVersionDetail()
    }

    private fun saveTheme(index:Int){
        if(MainActivity.themeIndex!=index){
            Toast.makeText(this,"${MainActivity.themeIndex}",Toast.LENGTH_SHORT).show()
            /***********************Storing the selected THEME******************************/
            val editor=getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex",index)
            editor.apply()

            val builder= MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme")
                .setMessage("Do you want to Apply Theme")
                .setPositiveButton("Yes"){_,_->
//                    /***********************Storing the selected THEME******************************/
//                    val editor=getSharedPreferences("THEMES", MODE_PRIVATE).edit                            IN THIS PROBLEM WHEN THE DATA IS STORING IT THE APPLICATION GET CLOSE SO IT IS NOT STORE PROPERLY
//                    editor.putInt("themeIndex",index)
//                    editor.apply()

                    exitApplication()
                }
                .setNegativeButton("No"){dialog,_->
                    dialog.dismiss()
                }
            val customDialog=builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

        }
    }

    private fun setVersionDetail():String{
        return "Version Name: ${BuildConfig.VERSION_NAME}"
    }
}