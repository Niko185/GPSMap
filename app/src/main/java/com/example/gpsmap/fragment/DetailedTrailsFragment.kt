package com.example.gpsmap.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.example.gpsmap.R
import com.example.gpsmap.database.instance.MainDataBaseInstanceInitialization
import com.example.gpsmap.databinding.FragmentDetailedTrailsBinding
import com.example.gpsmap.vm.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class DetailedTrailsFragment() : Fragment() {
    private lateinit var binding: FragmentDetailedTrailsBinding
    private var startPoint: GeoPoint? = null
    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((requireContext().applicationContext as MainDataBaseInstanceInitialization).dataBaseInstanceInitialization)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        basicConnectOSM()
        binding = FragmentDetailedTrailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getTrack()
        binding.fButtonCenter.setOnClickListener {
            if(startPoint != null) binding.map.controller.animateTo(startPoint)
        }

    }

    private fun basicConnectOSM() {
        Configuration.getInstance().load(activity as AppCompatActivity, activity?.getSharedPreferences("osm_pref_table_data", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }


    private fun getTrack() = with(binding ){
        mainViewModel.savedTrail.observe(viewLifecycleOwner) {
            tvTime.text = it.time
            tvDistance.text = it.distance
            tvDate.text = it.date
            tvAvarageVelocity.text = it.averageVelocity

            var polylined = getPolyline(it.geoPoints)
            map.overlays.add(polylined)
            setMarkers(polylined.actualPoints)
            goToStartPosition(polylined.actualPoints[0])
            startPoint = polylined.actualPoints[0]
        }
    }


    private fun goToStartPosition(startPosition: GeoPoint) {
        binding.map.controller.zoomTo(15.0)
        binding.map.controller.animateTo(startPosition)
    }

    private fun setMarkers(list: List<GeoPoint>) = with(binding) {
        val startMarker = Marker(map)
        val finishMarker = Marker(map)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        finishMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = getDrawable(requireContext(),R.drawable.ic_start_marker)
        finishMarker.icon = getDrawable(requireContext(), R.drawable.ic_finish_marker)
        startMarker.position = list[0]
        finishMarker.position = list[list.size - 1]
        map.overlays.add(startMarker)
        map.overlays.add(finishMarker)
    }

    private fun getPolyline(geoPoints: String): Polyline {
        var polyline = Polyline()
        polyline.outlinePaint.color = Color.parseColor(PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("color_key", "#FF000000", ))
        val list = geoPoints.split("/")
        Log.e("MyLog", "list $list")
        list.forEach {
            if(it.isEmpty()) return@forEach
            val points = it.split(",")
            polyline.addPoint(GeoPoint(points[0].toDouble(), points[1].toDouble()))
            Log.e("MyLog", "Thissssssss: $polyline")
        }
        return polyline
    }





    companion object {

        @JvmStatic
        fun newInstance() = DetailedTrailsFragment()
    }
}
