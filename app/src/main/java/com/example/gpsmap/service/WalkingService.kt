package com.example.gpsmap.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.gpsmap.R
import com.example.gpsmap.activity.MainActivity

class WalkingService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotificationWalkingService()
        isRunningService = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningService = false
    }

     private fun showNotificationWalkingService() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID_SERVICE, NAME_SERVICE, STATUS_SERVICE)
            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }


         val notificationIntent = Intent(this, MainActivity::class.java)
         val pendingIntent = PendingIntent.getActivity(
             this,
             0,
             notificationIntent,
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                 PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
             } else {
                 PendingIntent.FLAG_UPDATE_CURRENT
             }
         )
        val myNotification = NotificationCompat.Builder(this, CHANNEL_ID_SERVICE).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.walking_service_enable)).setContentIntent(pendingIntent).build()
        startForeground(99, myNotification)
    }

    companion object{
        const val CHANNEL_ID_SERVICE = "Channel_1"
        const val NAME_SERVICE = "Walking Service"
        const val STATUS_SERVICE = NotificationManager.IMPORTANCE_DEFAULT
        var isRunningService = false
    }
}