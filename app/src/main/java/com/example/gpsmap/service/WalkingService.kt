package com.example.gpsmap.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.gpsmap.R
import com.example.gpsmap.activity.MainActivity
import com.google.android.gms.location.*

class WalkingService : Service() {
    private var distance = 0.0f
    private var lastLocation: Location? = null
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotificationWalkingService()
        getUpdatesLocationInformation()
        isRunningService = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        initLocationProvider()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningService = false
        locationProvider.removeLocationUpdates(locationCallback)
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








   private val locationCallback = object : LocationCallback(){
       override fun onLocationResult(locationResult: LocationResult) {
           super.onLocationResult(locationResult)

           getWalkingDistance(locationResult)

       }
    }
    private fun initLocationProvider(){
        locationRequest = LocationRequest.create()
        locationRequest.interval = 7000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun getUpdatesLocationInformation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        } else

        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


    private fun getWalkingDistance(locationResult: LocationResult) {
        val currentLocationPhone = locationResult.lastLocation
        if(lastLocation != null && currentLocationPhone != null) {
            if(currentLocationPhone.speed > 0.5) distance = distance + lastLocation?.distanceTo(currentLocationPhone)!!
        } else
            lastLocation = currentLocationPhone
        Log.e("AAAAAAAAAA", "distance: $distance")
    }













    companion object{
        const val CHANNEL_ID_SERVICE = "Channel_1"
        const val NAME_SERVICE = "Walking Service"
        const val STATUS_SERVICE = NotificationManager.IMPORTANCE_DEFAULT

        var isRunningService = false
        var launchTime = 0L
    }
}