package com.example.gpsmap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gpsmap.R
import com.example.gpsmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBottomNavClicks()
    }

    private fun onBottomNavClicks(){
        binding.bNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.navigation -> Log.e("ItemCheck", "${menuItem.title}")
                R.id.trails -> Log.e("ItemCheck", "${menuItem.title}")
                R.id.settings -> Log.e("ItemCheck", "${menuItem.title}")
            }
                true
        }
    }

}