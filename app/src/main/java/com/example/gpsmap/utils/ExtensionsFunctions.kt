package com.example.gpsmap.utils

import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gpsmap.R


fun AppCompatActivity.openFragment(fragment: Fragment) {
    if(supportFragmentManager.fragments.isNotEmpty()) { if(supportFragmentManager.fragments[0].javaClass == fragment.javaClass) return }

    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolder, fragment)
        .commit()
}

fun Fragment.openFragment(fragment: Fragment) {
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolder, fragment)
        .commit()
}

fun Fragment.checkPermission(namePermission: String): Boolean {
    return when(PackageManager.PERMISSION_GRANTED) {
         ContextCompat.checkSelfPermission(activity as AppCompatActivity, namePermission) -> true
              else -> false
        }
    }



fun AppCompatActivity.showErrorLog(tag: String, text: String){
    Log.e(tag, text)
}

fun Fragment.showErrorLog(tag: String, text: String){
    Log.e(tag, text)
}

fun AppCompatActivity.showToast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(text: String) {
    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
}