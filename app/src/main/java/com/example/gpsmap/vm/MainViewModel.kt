package com.example.gpsmap.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gpsmap.models.LocationModel

class MainViewModel : ViewModel() {
    val mutableTimerDataView = MutableLiveData<String>()
    val locationDataLive = MutableLiveData<LocationModel>()

}