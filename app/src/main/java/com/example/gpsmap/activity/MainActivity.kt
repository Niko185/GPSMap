package com.example.gpsmap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gpsmap.R
import com.example.gpsmap.databinding.ActivityMainBinding
import com.example.gpsmap.fragment.MainFragment
import com.example.gpsmap.fragment.SettingsFragment
import com.example.gpsmap.fragment.TrailsFragment
import com.example.gpsmap.utils.openFragment
import com.example.gpsmap.utils.showErrorLog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBottomNavClicks()
        openFragment(MainFragment.newInstance())
    }

    private fun onBottomNavClicks(){
        binding.bNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navigation -> {
                    openFragment(MainFragment.newInstance())
                }
                R.id.trails -> {
                    openFragment(TrailsFragment.newInstance())
                }
                R.id.settings -> {
                    openFragment(SettingsFragment.newInstance())
                }
            }
                true
        }
    }

}