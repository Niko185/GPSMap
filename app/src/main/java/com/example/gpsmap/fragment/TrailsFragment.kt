package com.example.gpsmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpsmap.adapter.TrailsAdapter
import com.example.gpsmap.database.entity.TrailModel
import com.example.gpsmap.database.instance.MainDataBaseInstanceInitialization
import com.example.gpsmap.databinding.FragmentTrailsBinding
import com.example.gpsmap.utils.openFragment
import com.example.gpsmap.vm.MainViewModel


class TrailsFragment() : Fragment(), TrailsAdapter.Listener {
    private lateinit var binding: FragmentTrailsBinding
    private lateinit var myAdapter: TrailsAdapter
    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((requireContext().applicationContext as MainDataBaseInstanceInitialization).dataBaseInstanceInitialization)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        getTrailsObserve()
    }

    private fun initRecyclerView() = with(binding) {
        myAdapter = TrailsAdapter(this@TrailsFragment)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = myAdapter
    }

    private fun getTrailsObserve() {
        mainViewModel.allTrails.observe(viewLifecycleOwner) {
            myAdapter.submitList(it)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = TrailsFragment()
    }

    override fun onClick(model: TrailModel, clickType: TrailsAdapter.ClickType ) {
      when(clickType){
          TrailsAdapter.ClickType.OPEN -> {
              mainViewModel.savedTrail.value = model
              openFragment(DetailedTrailsFragment.newInstance())
          }
          TrailsAdapter.ClickType.DELETE -> {
              mainViewModel.deleteTrailModelFromDatabase(model)
          }
      }
    }
}
