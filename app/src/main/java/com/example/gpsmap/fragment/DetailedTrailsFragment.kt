package com.example.gpsmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gpsmap.databinding.FragmentDetailedTrailsBinding


class DetailedTrailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailedTrailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailedTrailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = DetailedTrailsFragment()
    }
}
