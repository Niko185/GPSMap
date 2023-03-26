package com.example.gpsmap.fragment

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gpsmap.databinding.FragmentMainBinding
import com.example.gpsmap.utils.checkPermission
import com.example.gpsmap.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainFragment : Fragment() {
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
        checkPermissions()

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
        permissionLauncherDialog = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if(it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                initOSM()
            } else {
                showToast("Not enable Location Permission")
            } 
        }
    }

    private fun checkPermissions() {
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
        } else {
            permissionLauncherDialog.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        }
    }

    private fun checkPermissionForVersionMin() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOSM()
        } else {
            permissionLauncherDialog.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
