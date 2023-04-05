package com.example.gpsmap.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.gpsmap.R
import com.example.gpsmap.database.entity.TrailModel
import com.example.gpsmap.database.instance.MainDataBaseInstanceInitialization
import com.example.gpsmap.databinding.FragmentMainBinding
import com.example.gpsmap.models.LocationModel
import com.example.gpsmap.service.WalkingService
import com.example.gpsmap.utils.checkPermission
import com.example.gpsmap.utils.dialog.DialogManager
import com.example.gpsmap.utils.showErrorLog
import com.example.gpsmap.utils.showToast
import com.example.gpsmap.utils.time.TimeManager
import com.example.gpsmap.vm.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*

class MainFragment : Fragment() {
    private var isServiceNowRunning: Boolean = false
    private var timer: Timer? = null
    private var launchTimeMyTrail = 0L
    private lateinit var binding: FragmentMainBinding
    private lateinit var permissionLauncherDialog : ActivityResultLauncher<Array<String>>
    private var polyLine: Polyline? = null
    private var firstStart: Boolean = true
   /* private var trailItemModel: TrailItemModel? = null*/
   private var locationModel: LocationModel? = null
    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((requireContext().applicationContext as MainDataBaseInstanceInitialization).dataBaseInstanceInitialization)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        basicConnectOSM()
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCallbackPermissions()
        setOnClicks()
        checkStateWalkingService()
        updateTextViewTimer()
        registerReceiver()
        instanceMainViewModel()

    }

    override fun onResume() {
        super.onResume()
        checkPermissionForAllVersion()
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity).unregisterReceiver(receiver)

    }


    private fun basicConnectOSM() {
        Configuration.getInstance().load(activity as AppCompatActivity, activity?.getSharedPreferences("osm_pref_table_data", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOSM() = with(binding){
        map.controller.setZoom(15.0)
        getMyLocationProvider()
        getMyLocationWithMarkerLayer()
        initPolyline()
    }

    private fun initPolyline(){
        polyLine = Polyline()
        polyLine?.outlinePaint?.color = Color.BLUE


    }

    private fun addPoint(list: List<GeoPoint>) {
        polyLine?.addPoint(list[list.size - 1])
    }

    private fun fillPolyline(list: List<GeoPoint>){
        list.forEach {
            polyLine?.addPoint(it)
        }
    }

    private fun isServiceWorkingWhenOpenApplication(list: List<GeoPoint>) {
         if(list.size  > 1 && firstStart){
             fillPolyline(list)
             firstStart = false
        } else {
            addPoint(list)
        }

    }



    private fun getMyLocationProvider(): GpsMyLocationProvider {
        val myLocationProvider = GpsMyLocationProvider(activity)
        return myLocationProvider
    }

    private fun getMyLocationWithMarkerLayer() {
        val myLocationLayer = MyLocationNewOverlay(getMyLocationProvider(), binding.map)
        myLocationLayer.enableMyLocation()
        myLocationLayer.enableFollowLocation()
        myLocationLayer.runOnFirstFix{
            binding.map.overlays.clear()
            binding.map.overlays.add(myLocationLayer)
            binding.map.overlays.add(polyLine)
        }
    }





    private fun instanceMainViewModel() = with(binding){
        mainViewModel.locationDataLive.observe(viewLifecycleOwner){
            val distance = "Distance: ${String.format("%.1f", it.distance / 1000)} km"
            val actionVelocity = "Action Velocity: ${String.format("%.1f", 3.6f * it.velocity)} km/h"
            val averageVelocity = "Average velocity: ${getAverageSpeed(it.distance)} km/h"
            tvDistance.text = distance
            tvActionVelocity.text = actionVelocity
            tvAvarageVelocity.text = averageVelocity
            locationModel = it
            isServiceWorkingWhenOpenApplication(it.markersGeoPointList)
            showErrorLog("AAAAAAAAAAAAAAAAAAAAAAA", "${it.distance} fdfdsfsfdsfdsfsddsfdsffdsfdfsd")
        }
    }
    private fun getAverageSpeed(distance: Float): String {
        return String.format("%.1f", 3.6f * (distance / ((System.currentTimeMillis() - launchTimeMyTrail) / 1000.0f)))
    }

    private fun geoPointsToString(list: List<GeoPoint>): String {
        val stringBuilder = StringBuilder()
        list.forEach {
            stringBuilder.append("${it.latitude}, ${it.longitude} / ")
        }
         return stringBuilder.toString()
    }



   private fun startWalkingService(){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           activity?.startForegroundService(Intent(activity, WalkingService::class.java))
       } else {
           activity?.startService(Intent(activity, WalkingService::class.java))
       }
       setImageStop()
       WalkingService.launchTime = System.currentTimeMillis()
       startTimer()
   }

    private fun stopWalkingService() {
        activity?.stopService(Intent(activity, WalkingService::class.java))
        setImagePlay()
        timer?.cancel()
    }

    private fun checkStateWalkingService() {
        isServiceNowRunning = WalkingService.isRunningService
        if(isServiceNowRunning) {
            setImageStop()
            startTimer()
        }
    }

    private fun controllingWalkingService() {
        if(!isServiceNowRunning) {
            startWalkingService()
        } else {
            stopWalkingService()
            val trailModel = getTrailItem()
            DialogManager.showSaveDialog(requireContext(), trailModel, object : DialogManager.Listener{
                override fun onClickDialogButton() {
                    mainViewModel.insertTrailModelInDatabase(trailModel)
                }

            })
        }
        isServiceNowRunning = !isServiceNowRunning
    }

    private fun getTrailItem(): TrailModel {
         return  TrailModel(
                id = null,
                time = showCurrentTimeInTimer(),
                date = TimeManager.getDate(),
                averageVelocity = getAverageSpeed(locationModel?.distance ?: 0.0f),
                actionVelocity = String.format("%.1f", 3.6 * locationModel?.velocity!!),
                distance = String.format("%.1f", locationModel?.distance?.div(1000) ?: 0),
                geoPoints = geoPointsToString(locationModel?.markersGeoPointList ?: listOf())
            )
    }

    private fun onClicks() : View.OnClickListener = with(binding) {
        return View.OnClickListener {
            when(it.id) {
                fButtonStartStop.id -> controllingWalkingService()
            }
        }
    }

    private fun setOnClicks() = with(binding ) {
        val onClicks = onClicks()
        fButtonStartStop.setOnClickListener(onClicks)
    }

    private fun setImageStop() {
        binding.fButtonStartStop.setImageResource(R.drawable.ic_stop)
    }

    private fun setImagePlay() {
        binding.fButtonStartStop.setImageResource(R.drawable.ic_play)
    }














    private fun showCurrentTimeInTimer(): String {
        var currentSystemTime = System.currentTimeMillis()
         return "Time: ${TimeManager.getTime(currentSystemTime - launchTimeMyTrail)}"
    }

    private fun updateTextViewTimer() {
        mainViewModel.mutableTimerDataView.observe(viewLifecycleOwner) { timeNow ->
            binding.tvTime.text = timeNow
        }
    }

    private fun startTimer() {
        timer?.cancel()

        timer = Timer()
        launchTimeMyTrail = WalkingService.launchTime
        timer?.schedule(object : TimerTask(){
            override fun run() {
               activity?.runOnUiThread{ mainViewModel.mutableTimerDataView.value = showCurrentTimeInTimer() }
            }
        }, 1, 1)
    }



















    private fun registerCallbackPermissions()  {
        permissionLauncherDialog = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                initOSM()
                checkGps()
            } else {
                    showToast(getString(R.string.location_request_not_enable))
            }
        }
    }

    private fun checkPermissionForAllVersion() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                checkPermissionForVersionMax()
        } else {
                checkPermissionForVersionMin()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionForVersionMax() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            initOSM()
            checkGps()
        } else {
            permissionLauncherDialog.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION))

        }
    }

    private fun checkPermissionForVersionMin() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOSM()
            checkGps()
        } else {
            permissionLauncherDialog.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun checkGps() {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!isEnabled) {
            DialogManager.showGpsDialog(activity as AppCompatActivity, object : DialogManager.Listener {
                override fun onClickDialogButton() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }



    private fun registerReceiver() {
        val filter = IntentFilter(WalkingService.BROADCAST_KEY_REZERVATION_INTENT_NAME)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity).registerReceiver(receiver, filter)
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WalkingService.BROADCAST_KEY_REZERVATION_INTENT_NAME) {
                val locModel =
                    intent.getSerializableExtra(WalkingService.BROADCAST_KEY_REZERVATION_INTENT_DATA) as LocationModel
                mainViewModel.locationDataLive.value = locModel
            }
        }
    }






    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
