package com.example.gpsmap.vm


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gpsmap.database.create.MainDataBase
import com.example.gpsmap.models.LocationModel
import androidx.lifecycle.*
import com.example.gpsmap.database.entity.TrailModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.lang.IllegalArgumentException


@Suppress ("UNCHECKED_CAST")
class MainViewModel(mainDataBase: MainDataBase) : ViewModel() {
    private val getDao = mainDataBase.getDao()
    val mutableTimerDataView = MutableLiveData<String>()
    val locationDataLive = MutableLiveData<LocationModel>()
    val savedTrail = MutableLiveData<TrailModel>()
    val allTrails = getDao.getAllSavedTrails().asLiveData()

    fun insertTrailModelInDatabase(trailModel: TrailModel) = viewModelScope.launch {
        getDao.insertTrailModelInDatabase(trailModel)
    }

    fun deleteTrailModelFromDatabase(trailModel: TrailModel) = viewModelScope.launch {
        getDao.deleteTrailModelFromDatabase(trailModel)
    }

    class MainViewModelFactory(private val mainDataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(mainDataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")

        }
    }
}