package com.example.gpsmap.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.example.gpsmap.R
import com.example.gpsmap.databinding.FragmentMainBinding
import com.example.gpsmap.service.WalkingService
import com.example.gpsmap.utils.checkPermission
import com.example.gpsmap.utils.dialog.DialogGps
import com.example.gpsmap.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
class MainFragment : Fragment() {
    private var isServiceNowRunning: Boolean = false
    private lateinit var binding: FragmentMainBinding
    private lateinit var permissionLauncherDialog : ActivityResultLauncher<Array<String>>








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
    }

    override fun onResume() {
        super.onResume()
        checkPermissionForAllVersion()
    }








    private fun basicConnectOSM() {
        Configuration.getInstance().load(activity as AppCompatActivity, activity?.getSharedPreferences("osm_pref_table_data", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOSM() = with(binding){
        map.controller.setZoom(15.0)
        getMyLocationProvider()
        getMyLocationWithMarkerLayer()
    }









   private fun startWalkingService(){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           activity?.startForegroundService(Intent(activity, WalkingService::class.java))
       } else {
           activity?.startService(Intent(activity, WalkingService::class.java))
       }
   }

    private fun stopWalkingService() {
        activity?.stopService(Intent(activity, WalkingService::class.java))
        setImagePlay()
    }

    private fun checkStateWalkingService() {
        isServiceNowRunning = WalkingService.isRunningService
        if(isServiceNowRunning) {
            setImageStop()
        } else setImagePlay()
    }

    private fun controllingWalkingService() {
        if(!isServiceNowRunning) {
            startWalkingService()
            setImageStop()
        } else stopWalkingService()

        isServiceNowRunning = !isServiceNowRunning
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
        }
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
            DialogGps.showGpsDialog(activity as AppCompatActivity, object : DialogGps.Listener {
                override fun onClickDialogButton() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }











    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
