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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.example.gpsmap.R
import com.example.gpsmap.activity.MainActivity
import com.example.gpsmap.models.LocationModel
import com.example.gpsmap.utils.showErrorLog
import com.example.gpsmap.vm.MainViewModel
import com.google.android.gms.location.*
import org.osmdroid.util.GeoPoint

class WalkingService : Service() {
    private var distance = 0.0f
    private var lastLocation: Location? = null
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var geoPointList: ArrayList<GeoPoint>


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setNotificationWalkingService()
        setUpdatesLocationInformation()
        isRunningService = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        geoPointList = ArrayList()
        initLocationProvider()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningService = false
        locationProvider.removeLocationUpdates(locationCallback)
    }











     private fun setNotificationWalkingService() {

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


    private fun setUpdatesLocationInformation() {
        // if not permission location -> not action, else get information
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        } else
            locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }


    private fun initLocationProvider(){
        val updateInterval = PreferenceManager.getDefaultSharedPreferences(this).getString("time_update_key", "3000")?.toLong() ?: 3000
        locationProvider = LocationServices.getFusedLocationProviderClient(baseContext)

        locationRequest = LocationRequest.create()
        locationRequest.interval = updateInterval
        locationRequest.fastestInterval = updateInterval
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
    }

   private val locationCallback = object : LocationCallback() {
       override fun onLocationResult(locationResult: LocationResult) {
           super.onLocationResult(locationResult)
           val currentLocationPhone = locationResult.lastLocation
           if (lastLocation != null && currentLocationPhone != null) {
               if (currentLocationPhone.speed > 0.7) {
                   distance += lastLocation?.distanceTo(currentLocationPhone)!!
                   geoPointList.add(
                       GeoPoint(
                           currentLocationPhone.latitude,
                           currentLocationPhone.longitude
                       )
                   ) 
               }
                   val locModel = LocationModel(
                       currentLocationPhone.speed,
                       distance,
                       geoPointList
                   )
                   sendServiceDataOnFragment(locModel)
               }
               lastLocation = currentLocationPhone
           }
       }





    private fun sendServiceDataOnFragment(locationModel: LocationModel) {
        val intent = Intent(BROADCAST_KEY_REZERVATION_INTENT_NAME)
        intent.putExtra(BROADCAST_KEY_REZERVATION_INTENT_DATA, locationModel)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }














    companion object{
        const val CHANNEL_ID_SERVICE = "Channel_1"
        const val NAME_SERVICE = "Walking Service"
        @RequiresApi(Build.VERSION_CODES.N)
        const val STATUS_SERVICE = NotificationManager.IMPORTANCE_DEFAULT
        const val BROADCAST_KEY_REZERVATION_INTENT_NAME = "location_model_intent"
        const val BROADCAST_KEY_REZERVATION_INTENT_DATA = "location_model_intent_data"

        var isRunningService = false
        var launchTime = 0L
    }
}