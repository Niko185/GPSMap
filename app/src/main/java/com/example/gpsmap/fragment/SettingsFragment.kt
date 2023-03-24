package com.example.gpsmap.fragment


import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.example.gpsmap.R


class SettingsFragment :  PreferenceFragmentCompat() {
    private lateinit var timePref: Preference
    private lateinit var colorPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_screen, rootKey)
        initPrefElements()
    }

    private fun initPrefElements() {
        timePref = findPreference("time_update_key")!!
        colorPref = findPreference("color_key")!!

        val changeListenerTime = onChangeListenerTimePref()
        val changeListenerColor = onChangeListenerColorPref()

        timePref.onPreferenceChangeListener = changeListenerTime
        colorPref.onPreferenceChangeListener = changeListenerColor

        saveAllPreference()
    }

    private fun onChangeListenerTimePref(): OnPreferenceChangeListener {
        return  OnPreferenceChangeListener {
            prefItem, newValue ->

            val arrayName = resources.getStringArray(R.array.location_time_update_name)
            val arrayValue = resources.getStringArray(R.array.location_time_update_value)
            val position = arrayValue.indexOf(newValue)
            val title = prefItem.title.toString().substringBefore(":")
            prefItem.title = "$title: ${arrayName[position]}"

            true
        }
    }


    private fun onChangeListenerColorPref(): OnPreferenceChangeListener {
        return  OnPreferenceChangeListener {
                prefItem, newValue ->

            val arrayNameColor = resources.getStringArray(R.array.color_name)
            val arrayValueColor = resources.getStringArray(R.array.color_value)
            val pos = arrayValueColor.indexOf(newValue)
            val titleColor = prefItem.title.toString().substringBefore(":")
            prefItem.title = "$titleColor: ${arrayNameColor[pos]}"

            true
        }
    }

    private fun saveAllPreference() {
        val savedPref = timePref.preferenceManager.sharedPreferences
        val arrayName = resources.getStringArray(R.array.location_time_update_name)
        val arrayValue = resources.getStringArray(R.array.location_time_update_value)
        val position = arrayValue.indexOf(savedPref?.getString("time_update_key", "3000"))
        val title = timePref.title
        timePref.title = "$title: ${arrayName[position]}"

        val arrayNameColor = resources.getStringArray(R.array.color_name)
        val arrayValueColor = resources.getStringArray(R.array.color_value)
        val pos = arrayValueColor.indexOf(savedPref?.getString("color_key", R.color.black.toString()))
        val titleColor = colorPref.title
        colorPref.title = "$titleColor: ${arrayNameColor[pos]}"
    }
}







